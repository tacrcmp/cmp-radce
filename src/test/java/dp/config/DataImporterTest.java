package dp.config;

import com.mysql.cj.jdbc.MysqlDataSource;
import dp.dao.CategoryDAO;
import dp.dao.DocumentDAO;
import dp.dao.KeyWordsDAO;
import dp.dao.NgramDAO;
import dp.keyWords.TfIdfCalculator;
import dp.services.DataImporter;
import dp.services.DocumentsImporter;
import dp.services.KeyWordsImporter;
import dp.services.NgramsImporter;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.sql.SQLException;

@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
public class DataImporterTest {
    private DataImporter dataImporter;

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
        NgramsImporter ngramsImporter = new NgramsImporter(categoryDAO, documentDAO, ngramDAO);
        KeyWordsDAO keyWordsDAO = new KeyWordsDAO(ds);
        KeyWordsImporter keyWordsImporter = new KeyWordsImporter(new TfIdfCalculator(documentDAO, categoryDAO), keyWordsDAO);

        this.dataImporter = new DataImporter(ngramsImporter,
                new DocumentsImporter(categoryDAO, documentDAO), keyWordsImporter);
    }

    @Ignore
    @Test
    public void shouldLoadMalciksTexts() throws IOException, SQLException {
        dataImporter.importData("C:\\Users\\Barča\\Documents\\VŠ\\DP\\texty2\\train" , false);
    }

    @Ignore
    @Test
    public void shouldLoadOsuTexts() throws IOException, SQLException {
        dataImporter.importData("C:\\Users\\Barča\\Documents\\VŠ\\DP\\texty3\\train" , false);
    }

    @Ignore
    @Test
    public void shouldLoadLanguagesTexts() throws IOException, SQLException {
        dataImporter.importData("C:\\Users\\Barča\\Documents\\VŠ\\DP\\texty4\\train" , false);
    }

    @Ignore
    @Test
    public void shouldLoadMalciksTexts2() throws IOException, SQLException {
        dataImporter.importData("C:\\Users\\Barča\\Documents\\VŠ\\DP\\texty1\\train", false);
    }

    @Ignore
    @Test
    public void shouldLoadSentimentSentences() throws IOException, SQLException {
        dataImporter.importData("C:\\Users\\Barča\\Documents\\VŠ\\DP\\texty5\\train",  false);
    }

    @Test
    public void shouldLoadCmpRadceTexts() throws IOException, SQLException {
        dataImporter.importData("/home/andre444/development/vsb-tuo/cmp-radce/data/cmp-radce.cz - combined data/train", true);
    }
}
