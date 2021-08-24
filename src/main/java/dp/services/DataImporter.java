package dp.services;

import dp.dao.KeyWordsDAO;
import dp.keyWords.KeyWorldsCalculator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@Service
public class DataImporter {
    private final NgramsImporter ngramsImporter;
    private final DocumentsImporter documentsImporter;
    private final KeyWordsImporter keyWordsImporter;

    @Autowired
    public DataImporter(NgramsImporter ngramsImporter,
                        DocumentsImporter documentsImporter,
                        KeyWordsImporter keyWordsImporter) {
        this.ngramsImporter = ngramsImporter;
        this.keyWordsImporter = keyWordsImporter;
        this.documentsImporter = documentsImporter;
    }

    public void importData(String pathToDocs, boolean loadKeyWords) throws IOException, SQLException {
        documentsImporter.importTexts(pathToDocs);
        ngramsImporter.importNgrams();

        if(loadKeyWords) {
            keyWordsImporter.importKeyWords();
        }
    }


}
