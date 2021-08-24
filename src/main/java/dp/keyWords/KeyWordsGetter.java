package dp.keyWords;

import com.google.common.collect.Lists;
import dp.dao.CategoryDAO;
import dp.dao.KeyWordsDAO;
import dp.dto.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class KeyWordsGetter {
    private final CategoryDAO categoryDAO;
    private final KeyWordsDAO keyWordsDAO;

    @Autowired
    public KeyWordsGetter(CategoryDAO categoryDAO, KeyWordsDAO keyWordsDAO) {
        this.categoryDAO = categoryDAO;
        this.keyWordsDAO = keyWordsDAO;
    }

    public List<String> getKeyWords() throws SQLException {
        List<String> result = Lists.newArrayList();

        List<Integer> categories = categoryDAO.getCategories().stream().map(Category::getId).collect(Collectors.toList());
        Map<Integer, List<String>> keyWords = keyWordsDAO.getKeyWords(categories);
        keyWords.forEach((key, value) -> result.addAll(value));

        return result;
    }

    public Map<Integer, List<String>> getKeyWordsPerCat() throws SQLException {
        List<Integer> categories = categoryDAO.getCategories().stream().map(Category::getId).collect(Collectors.toList());
        return keyWordsDAO.getKeyWords(categories);
    }
}
