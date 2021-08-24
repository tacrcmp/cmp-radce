package dp.services;


import dp.dao.CategoryDAO;
import dp.dao.DocumentDAO;
import dp.dao.LastSyncDAO;
import dp.dto.Category;
import dp.dto.Document;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.sql.SQLException;
import java.time.Instant;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
public class Syncer {
    private static AtomicBoolean syncReady = new AtomicBoolean(true);
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private final CategoryDAO categoryDAO;
    private final DocumentDAO documentDAO;
    private final NgramsImporter ngramsImporter;
    private final KeyWordsImporter keyWordsImporter;
    private final LastSyncDAO lastSyncDAO;

    private static final Logger LOGGER = LoggerFactory.getLogger(Syncer.class);

    @Autowired
    public Syncer(CategoryDAO categoryDAO,
                  DocumentDAO documentDAO,
                  NgramsImporter ngramsImporter,
                  KeyWordsImporter keyWordsImporter,
                  LastSyncDAO lastSyncDAO) {
        this.categoryDAO = categoryDAO;
        this.documentDAO = documentDAO;
        this.ngramsImporter = ngramsImporter;
        this.keyWordsImporter = keyWordsImporter;
        this.lastSyncDAO = lastSyncDAO;
    }

    public static boolean isSyncReady() {
        return syncReady.get();
    }

    public void sync(JSONObject data) {
        scheduler.execute(() -> {
            try {
                syncReady.set(false);

                JSONArray objects = data.getJSONArray("objects");

                for (Object object : objects) {
                    JSONObject jsonObject = (JSONObject) object;
                    Integer postId = getIntegerCheckedForNullAndEmptyValue(jsonObject, "postId");
                    String postContent = jsonObject.getString("postContent");
                    Integer categoryId = getIntegerCheckedForNullAndEmptyValue(jsonObject, "categoryId");
                    String categoryName = jsonObject.getString("categoryName");

                    Integer dbCategoryId = null;
                    if (categoryId != null && categoryId > 0) {
                        Category category = categoryDAO.getCategoryById(categoryId);
                        if (!category.getName().equals(categoryName)) {
                            throw new IllegalStateException(String.format("Category name '%s' does not match with the stored one '%s' for ID %d", categoryName, category.getName(), categoryId));
                        }
                        dbCategoryId=categoryId;
                    } else {
                        dbCategoryId = categoryDAO.getCategoryId(categoryName);
                        if (dbCategoryId == -1) {
                            dbCategoryId = categoryDAO.putCategory(new Category(categoryName));
                        }
                    }



                    Document document = null;
                    if (postId != null) {
                        document = documentDAO.getDocument(postId, "WP");
                    }
                    if (document == null) {
                        Document newDoc = new Document(postId, "WP", postContent, dbCategoryId);
                        documentDAO.putDocument(newDoc);
                    } else {
                        document.setText(postContent);
                        document.setCategoryId(dbCategoryId);
                        documentDAO.updateDocument(document);
                    }
                }

                ngramsImporter.importNgrams();
                keyWordsImporter.importKeyWords();

                lastSyncDAO.putLastSync(Date.from(Instant.now()));
                syncReady.set(true);
            } catch (Throwable e) {
                LOGGER.error("Synchronization failed",e);
                throw new RuntimeException(e);
            }
        });
    }

    private Integer getIntegerCheckedForNullAndEmptyValue(JSONObject jsonObject, String key) {
        if (!jsonObject.has(key)) {
            return null;
        }

        if (jsonObject.get(key) == null) {
            return null;
        }

        if (String.valueOf(jsonObject.get(key)).isEmpty()) {
            return null;
        }

        return jsonObject.getInt(key);
    }
}
