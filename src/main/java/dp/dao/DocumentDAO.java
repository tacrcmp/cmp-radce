package dp.dao;

import com.google.common.base.Preconditions;
import dp.dto.Document;
import dp.mappers.DocumentMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DocumentDAO {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public DocumentDAO (JdbcTemplate jdbcTemplate){
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Document> getDocuments(){
        return jdbcTemplate.query("select * from Document", new DocumentMapper());
    }

    public Document getDocument(int id, String source){
        List<Document> docs = jdbcTemplate.query("select * from Document where id = ? and source = ?", new Object[]{id, source}, new DocumentMapper());
        Preconditions.checkArgument(docs.size() <= 1);
        return docs.isEmpty()? null : docs.get(0);
    }

    public void putDocument(Document document){
        if(document.getId() != null){
            jdbcTemplate.update("insert INTO Document(id, text, source, category_id) values (?, ?, ?, ?)",
                    document.getId(), document.getText(), document.getSource(), document.getCategoryId());
        } else {
            jdbcTemplate.update("insert INTO Document(text, source, category_id) values (?, ?, ?)",
                    document.getText(), document.getSource(), document.getCategoryId());
        }
    }

    public List<Document> getDocumentByCategory(int categoryId){
        return jdbcTemplate.query("select * from Document where category_id = ?", new Object[]{categoryId}, new DocumentMapper());
    }

    public int getDocumentByText(String text){
        List<Document> docs = jdbcTemplate.query("select * from Document where text = ?", new Object[]{text}, new DocumentMapper());
        Preconditions.checkArgument(docs.size() <= 1);

        if(docs.isEmpty()){
            return -1;
        }
        return docs.get(0).getId();
    }

    public void updateDocument(Document document){
        jdbcTemplate.update("update Document set category_id = ?, text = ? where id = ? and source = ?",
                document.getCategoryId(), document.getText(), document.getId(), document.getSource());
    }
}
