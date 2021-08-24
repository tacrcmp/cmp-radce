package dp.config;

import com.mysql.cj.jdbc.MysqlDataSource;
import dp.categorization.Categorization;
import dp.categorization.ngram.NgramCategorization;
import dp.dao.CategoryDAO;
import dp.dao.NgramDAO;
import dp.resources.CategorizationResource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.sql.SQLException;

@Configuration
public class BeanConfig {

    @Bean
    public DataSource dataSource(@Value("${mysql.url}") String url, @Value("${mysql.user}") String user, @Value("${mysql.pass}") String pass) {
        MysqlDataSource ds = new MysqlDataSource();
        ds.setURL(url);
        ds.setUser(user);
        ds.setPassword(pass);
        return ds;
    }

    @Bean
    public JdbcTemplate jdbcTemplate(DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

}
