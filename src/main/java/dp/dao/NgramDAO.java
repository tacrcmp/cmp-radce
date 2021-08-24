package dp.dao;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.*;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class NgramDAO {
    private static final Logger LOGGER = LoggerFactory.getLogger(NgramDAO.class);
    private final Connection connection;

    @Autowired
    public NgramDAO(DataSource dataSource) throws SQLException {
        this.connection = dataSource.getConnection();
    }

    public List<String> getNgramsOfProfile(int categoryId) throws SQLException {
        Map<String, Integer> result = Maps.newLinkedHashMap();

        ResultSet resultSet = connection.createStatement().executeQuery("select * from category_profile_item where category_id = " + categoryId);
        while (resultSet.next()) {
            Integer ngram_id = resultSet.getInt("ngram_id");
            Integer position = resultSet.getInt("position");

            ResultSet resultSetNgram = connection.createStatement().executeQuery("select * from ngram where id = " + ngram_id);
            while (resultSetNgram.next()) {
                String ngram = resultSetNgram.getString("ngram");
                result.put(ngram, position);
            }
        }

        //sort by position
        result = result.entrySet().stream()
                .sorted(Map.Entry.comparingByValue())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));

        return Lists.newArrayList(result.keySet());
    }

    public void putNgramsPerCategory(Integer categoryId, List<String> ngrams) throws SQLException {
        Map<Integer, Integer> ngramIdsPosition = Maps.newLinkedHashMap();

        PreparedStatement pSNgrams = connection.prepareStatement("insert INTO ngram(ngram) values (?)", Statement.RETURN_GENERATED_KEYS);
        for (int i = 0; i < ngrams.size(); i++) {
            try {
                pSNgrams.setString(1, ngrams.get(i));
                pSNgrams.executeUpdate();

                ResultSet rs = pSNgrams.getGeneratedKeys();
                if (rs.next()) {
                    ngramIdsPosition.put(rs.getInt(1), i + 1);
                }
            } catch (SQLIntegrityConstraintViolationException e) {
                LOGGER.debug("Existing ngram: {}", ngrams.get(i));
                ngramIdsPosition.put(getNgramId(ngrams.get(i)), i + 1);
            }
        }

        PreparedStatement psNgramsCat = connection.prepareStatement("insert INTO category_profile_item(category_id, ngram_id, position) values (?, ?, ?)");
        for (Map.Entry<Integer, Integer> entry : ngramIdsPosition.entrySet()) {
            psNgramsCat.setInt(1, categoryId);
            psNgramsCat.setInt(2, entry.getKey());
            psNgramsCat.setInt(3, entry.getValue());
            psNgramsCat.executeUpdate();
        }
    }

    public void deleteProfileOfCategory(int categoryId) throws SQLException {
        connection.createStatement().executeUpdate("delete from category_profile_item where category_id = '" + categoryId + "'");
    }

    private Integer getNgramId(String ngram) throws SQLException {
        ResultSet resultSetNgram = connection.createStatement().executeQuery("select * from ngram where ngram = '" + ngram + "'");
        if (resultSetNgram.next()) {
            return resultSetNgram.getInt("ngram_id");
        } else {
            throw new IllegalArgumentException("Ngram " + ngram + " doesn't exists.");
        }
    }
}
