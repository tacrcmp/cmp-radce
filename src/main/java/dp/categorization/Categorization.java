package dp.categorization;

import dp.dto.Category;
import dp.dto.Document;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public interface Categorization {

    boolean checkCategorize(Document document, List<Category> categories) throws IOException, SQLException;

    boolean checkCategorize(Document document) throws IOException, SQLException;

    Category categorize(Document document) throws IOException, SQLException;

    default Category categorize(String text) throws IOException, SQLException{
        Document document = new Document(text);
        return categorize(document);
    }
}
