package dp.services;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import dp.categorization.ngram.ProfileCalculator;
import dp.dao.CategoryDAO;
import dp.dao.DocumentDAO;
import dp.dao.NgramDAO;
import dp.dto.Category;
import dp.dto.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@Service
public class NgramsImporter {
    private final CategoryDAO categoryDAO;
    private final DocumentDAO documentDAO;
    private final NgramDAO ngramDAO;

    @Autowired
    public NgramsImporter(CategoryDAO categoryDAO, DocumentDAO documentDAO, NgramDAO ngramDAO){
        this.categoryDAO = categoryDAO;
        this.documentDAO = documentDAO;
        this.ngramDAO = ngramDAO;
    }

    public void importNgrams() throws IOException, SQLException {
        List<Category> categories = categoryDAO.getCategories();
        Map<Integer, List<Document>> docsPerCategory = Maps.newHashMap();
        for (Category category : categories) {
            List<Document> documentsByCategory = documentDAO.getDocumentByCategory(category.getId());
            for (Document document : documentsByCategory) {
                docsPerCategory.putIfAbsent(document.getCategoryId(), Lists.newArrayList());
                docsPerCategory.get(document.getCategoryId()).add(document);
            }
        }

        for (Map.Entry<Integer, List<Document>> entry : docsPerCategory.entrySet()) {
            ngramDAO.deleteProfileOfCategory(entry.getKey());
            List<String> profile = ProfileCalculator.calculateCategoryProfile(entry.getValue());
            ngramDAO.putNgramsPerCategory(entry.getKey(), profile);
        }
    }
}
