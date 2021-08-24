package dp.dao;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.*;
import java.util.List;
import java.util.Map;

@Service
public class KeyWordsDAO {
    private final Connection connection;

    @Autowired
    public KeyWordsDAO(DataSource dataSource) throws SQLException {
        this.connection = dataSource.getConnection();
    }

    public void putKeyWords(Map<Integer, List<String>> keyWordsPerCategory) throws SQLException {
        PreparedStatement psKeyWords = connection.prepareStatement("insert INTO keyword(word) values (?)", Statement.RETURN_GENERATED_KEYS);
        PreparedStatement psKeyWordsCat = connection.prepareStatement("insert INTO keyword_category(category_id, keyWord_id) values (?, ?)");

        for (Map.Entry<Integer, List<String>> entry : keyWordsPerCategory.entrySet()) {
            for (String keyWord : entry.getValue()) {
                int keyWordId = getKeyWordIdByName(keyWord);

                if (keyWordId == -1) {
                    psKeyWords.setString(1, keyWord);
                    psKeyWords.executeUpdate();

                    ResultSet rs = psKeyWords.getGeneratedKeys();
                    if (rs.next()) {
                        keyWordId = rs.getInt(1);
                    }
                }

                psKeyWordsCat.setInt(1, entry.getKey());
                psKeyWordsCat.setInt(2, keyWordId);
                psKeyWordsCat.executeUpdate();
            }
        }
    }

    public Map<Integer, List<String>> getKeyWords(List<Integer> categoryIds) throws SQLException {
        Map<Integer, List<String>> result = Maps.newLinkedHashMap();

        for (Integer categoryId : categoryIds) {
            List<String> keyWordsForCategory = getKeyWordsForCategory(categoryId);
            result.put(categoryId, keyWordsForCategory);
        }

        return result;
    }

    private List<String> getKeyWordsForCategory(Integer categoryId) throws SQLException {
        List<String> result = Lists.newArrayList();

        ResultSet resultSet = connection.createStatement().executeQuery("select * from keyword_category where category_id = " + categoryId);
        while (resultSet.next()) {
            Integer keyWordsId = resultSet.getInt("keyWord_id");

            ResultSet rsKeyWords = connection.createStatement().executeQuery("select * from keyword where id = " + keyWordsId);
            while (rsKeyWords.next()) {
                String word = rsKeyWords.getString("word");

                result.add(word);
            }
        }

        return result;
    }

    public void deleteKeyWordsByCatId(int categoryId) throws SQLException {
        connection.createStatement().executeUpdate("delete from keyword_category where category_id = " + categoryId);
    }

    private int getKeyWordIdByName(String keyWord) throws SQLException {
        ResultSet resultSet = connection.createStatement().executeQuery("select id from keyword where word = '" + keyWord + "'");
        if (resultSet.next()) {
            return resultSet.getInt("id");
        }
        return -1;
    }
}
