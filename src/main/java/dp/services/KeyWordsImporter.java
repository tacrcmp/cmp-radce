package dp.services;

import dp.dao.KeyWordsDAO;
import dp.keyWords.KeyWorldsCalculator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@Service
public class KeyWordsImporter {
    private final KeyWorldsCalculator keyWorldsCalculator;
    private final KeyWordsDAO keyWordsDAO;

    @Autowired
    public KeyWordsImporter(KeyWorldsCalculator keyWorldsCalculator, KeyWordsDAO keyWordsDAO){
        this.keyWordsDAO = keyWordsDAO;
        this.keyWorldsCalculator = keyWorldsCalculator;
    }

    public void importKeyWords() throws IOException, SQLException {
        Map<Integer, List<String>> categoriesKeywords = keyWorldsCalculator.calculateCatKeywords();
        for (Integer catId : categoriesKeywords.keySet()) {
            keyWordsDAO.deleteKeyWordsByCatId(catId);
        }

        keyWordsDAO.putKeyWords(categoriesKeywords);
    }
}
