package dp.dao;

import com.google.common.base.Preconditions;
import dp.dto.Category;
import dp.mappers.CategoryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

@Repository
public class CategoryDAO {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public CategoryDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Category> getCategories() {
        return jdbcTemplate.query("select * from Category", new CategoryMapper());
    }

    public Integer putCategory(Category category) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(
                new PreparedStatementCreator() {
                    public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                        PreparedStatement pst = con.prepareStatement("insert INTO Category(name) values (?)", new String[]{"id"});
                        pst.setString(1, category.getName());
                        return pst;
                    }
                },
                keyHolder);
        return keyHolder.getKey().intValue();
    }

    public Integer getCategoryId(String name) {
        List<Category> category = jdbcTemplate.query("select * from Category where name = ?", new Object[]{name}, new CategoryMapper());
        Preconditions.checkArgument(category.size() <= 1);
        return category.isEmpty() ? -1 : category.get(0).getId();
    }

    public Category getCategoryById(int id) {
        List<Category> category = jdbcTemplate.query("select * from Category where id = ?", new Object[]{id}, new CategoryMapper());
        Preconditions.checkArgument(category.size() <= 1);
        return category.isEmpty() ? null : category.get(0);
    }
}
