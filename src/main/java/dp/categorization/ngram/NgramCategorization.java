package dp.categorization.ngram;

import com.google.common.collect.Lists;
import dp.dao.CategoryDAO;
import dp.dao.KeyWordsDAO;
import dp.dao.NgramDAO;
import dp.dto.Category;
import dp.dto.Document;
import dp.categorization.Categorization;
import dp.keyWords.KeyWordsGetter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Service
public class NgramCategorization implements Categorization {
    private class MinDistanceHolder {
        private int distance;
        private Category category;

        private MinDistanceHolder(int distance, Category category) {
            this.distance = distance;
            this.category = category;
        }

        private int getDistance() {
            return distance;
        }

        private Category getCategory() {
            return category;
        }
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(NgramCategorization.class);
    private final CategoryDAO categoryDAO;
    private final NgramDAO ngramDAO;
    //private final List<Category> categories;

    @Autowired
    public NgramCategorization(CategoryDAO categoryDAO, NgramDAO ngramDAO) {
        this.categoryDAO = categoryDAO;
        this.ngramDAO = ngramDAO;
        //this.categories = categoryDAO.getCategories(1);
    }

    @Override
    public boolean checkCategorize(Document document, List<Category> categories) throws IOException, SQLException {
        return check(document, categories);
    }

    @Override
    public boolean checkCategorize(Document document) throws IOException, SQLException {
        List<Category> categories = categoryDAO.getCategories();
        return check(document, categories);
    }

    @Override
    public Category categorize(Document document) throws IOException, SQLException {
        List<Category> categories = categoryDAO.getCategories();
        return getCategory(document, categories);
    }

    private boolean check(Document document, List<Category> categories) throws IOException, SQLException {
        Category definedCategory = getCategory(document, categories);

        //categorize doc to the closest category
        if (!categories.isEmpty() && definedCategory != null) {
            if (document.getCategoryId() != null) {
                if(document.getCategory() == null){
                    Optional<Category> category = categories.stream().filter(c -> c.getId() == document.getCategoryId()).findFirst();
                    category.ifPresent(document::setCategory);
                }

                boolean correctCategorization = document.getCategoryId() == definedCategory.getId();
                if (!correctCategorization) {
                    LOGGER.info("Wrong categorization. Expected {}, result {}.", document.getCategory().getName(), definedCategory.getName());
                } else {
                    LOGGER.info("Correct categorization. Document was categorize to category {}.", document.getCategory().getName());
                }

                return correctCategorization;
            } else {
                document.setCategory(definedCategory);
                LOGGER.info("Document was categorize to category {}.", document.getCategory().getName());
            }
        }

        return false;
    }

    private Category getCategory(Document document, List<Category> categories) throws IOException, SQLException {
        //calculate profile for document
        List<String> docProfile = ProfileCalculator.calculateDocProfile(document);

        //measure distance between doc's profile and each category's profile
        MinDistanceHolder minDistanceHolder = null;
        for (Category category : categories) {
            List<String> categoryNgrams = Lists.newArrayList();
            if (category.getProfile() == null) {
                categoryNgrams = ngramDAO.getNgramsOfProfile(category.getId());
                category.setProfile(categoryNgrams);
            } else {
                categoryNgrams = category.getProfile();
            }

            int distance = DistanceMeasurement.getDistance(docProfile, categoryNgrams);

            if (minDistanceHolder == null || distance < minDistanceHolder.getDistance()) {
                minDistanceHolder = new MinDistanceHolder(distance, category);
            }
        }

        //categorize doc to the closest category
        if (!categories.isEmpty()) {
            Category category = minDistanceHolder.getCategory();
            return category;
        }

        return null;
    }
}
