package dp.categorization.ngram;

import com.google.common.collect.Lists;
import com.mysql.cj.jdbc.MysqlDataSource;
import dp.services.DocumentsImporter;
import dp.dao.CategoryDAO;
import dp.dao.DocumentDAO;
import dp.dao.KeyWordsDAO;
import dp.dao.NgramDAO;
import dp.dto.Category;
import dp.dto.Document;
import dp.keyWords.KeyWordsGetter;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class NgramCategorizationTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(NgramCategorizationTest.class);
    private NgramCategorization ngramCategorization;
    private CategoryDAO categoryDAO;
    private DocumentsImporter documentsImporter;

    @Before
    public void init() throws SQLException {
        MysqlDataSource ds = new MysqlDataSource();
        ds.setURL("jdbc:mysql://127.0.0.1/cmpradce?useUnicode=true&characterEncoding=UTF-8&serverTimezone=UTC");
        ds.setUser("cmpradce");
        ds.setPassword("cmpradce");

        JdbcTemplate jdbcTemplate = new JdbcTemplate(ds);
        CategoryDAO categoryDAO = new CategoryDAO(jdbcTemplate);
        DocumentDAO documentDAO = new DocumentDAO(jdbcTemplate);
        NgramDAO ngramDAO = new NgramDAO(ds);

        this.documentsImporter = new DocumentsImporter(categoryDAO, documentDAO);
        this.ngramCategorization = new NgramCategorization(categoryDAO, ngramDAO);
        this.categoryDAO = categoryDAO;
    }

    @Ignore
    @Test
    public void shouldCategorizeMalciksTexts() throws IOException, SQLException {
        process(new File("C:\\Users\\Barča\\Documents\\VŠ\\DP\\texty2\\test"));
    }

    @Ignore
    @Test
    public void shouldCategorizeOsuTexts() throws IOException, SQLException {
        process(new File("C:\\Users\\Barča\\Documents\\VŠ\\DP\\texty3\\test"));
    }

    @Ignore
    @Test
    public void shouldCategorizeLanguages() throws IOException, SQLException {
        process(new File("C:\\Users\\Barča\\Documents\\VŠ\\DP\\texty4\\test"));
    }

    @Ignore
    @Test
    public void shouldCategorizeMalciksTexts2() throws IOException, SQLException {
       process(new File("C:\\Users\\Barča\\Documents\\VŠ\\DP\\texty1\\test"));
    }

    @Ignore
    @Test
    public void shouldCategorizeSentimentSentences() throws IOException, SQLException {
        process(new File("C:\\Users\\Barča\\Documents\\VŠ\\DP\\texty5\\test"));
    }

    @Test
    public void shouldCategorizeCmpRadceTexts() throws IOException, SQLException {
        process(new File("/home/andre444/development/vsb-tuo/cmp-radce/data/cmp-radce.cz - combined data/test"));
    }

    private void process(File textsFile) throws IOException, SQLException {
        int correct = 0, wrong = 0;
        List<Document> documents = Lists.newArrayList();
        List<Category> categories = categoryDAO.getCategories();

        File[] files = textsFile.listFiles();
        if (files != null) {
            for (File file : Lists.newArrayList(files)) {
                if (file.isDirectory()) {
                    File[] docs = file.listFiles();
                    for (File doc : docs) {
                        documents.add(documentsImporter.importDocument(doc.getAbsolutePath()));
                    }
                } else {
                    documents.add(documentsImporter.importDocument(file.getAbsolutePath()));
                }
            }
        } else {
            throw new IllegalArgumentException("Dir " + textsFile.getAbsolutePath() + " is empty.");
        }

        for (Document document : documents) {
            boolean categorize = ngramCategorization.checkCategorize(document, categories);

            if (categorize) {
                correct++;
            } else {
                wrong++;
            }
        }

        LOGGER.info("{} correct, {} wrong. {}% correct.", correct, wrong, (correct * 100) /(correct + wrong));
    }

}
