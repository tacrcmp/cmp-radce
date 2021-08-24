package dp.mappers;

import dp.dao.CategoryDAO;
import dp.dto.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class DocumentMapper implements RowMapper<Document> {

    @Override
    public Document mapRow(ResultSet resultSet, int i) throws SQLException {
        Document document = new Document();
        document.setId(resultSet.getInt("id"));
        document.setText(resultSet.getString("text"));
        document.setCategoryId(resultSet.getInt("category_id"));
        document.setSource(resultSet.getString("source"));
        return document;
    }
}
