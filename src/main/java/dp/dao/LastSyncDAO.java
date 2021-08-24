package dp.dao;

import com.google.common.base.Preconditions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Repository
public class LastSyncDAO {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public LastSyncDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void putLastSync(Date lastSync) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        jdbcTemplate.update("insert INTO lastsync(lastSync) values (?)", sdf.format(lastSync));
    }

    public Date getLastSync(){
        List<Map<String, Object>> rows = jdbcTemplate.queryForList("select * from lastsync order by lastSync desc limit 1");
        Preconditions.checkArgument(rows.size() <= 1);

        Date lastUpdate = null;
        for (Map<String, Object> row : rows) {
            Timestamp timestamp = (Timestamp) row.get("lastSync");
            lastUpdate = new Date(timestamp.getTime());
        }

        return lastUpdate;
    }
}
