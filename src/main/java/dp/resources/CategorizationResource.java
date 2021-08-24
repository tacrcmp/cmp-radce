package dp.resources;

import dp.categorization.Categorization;
import dp.dao.CategoryDAO;
import dp.dao.LastSyncDAO;
import dp.dto.Category;
import dp.keyWords.KeyWordsGetter;
import dp.services.Syncer;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Component
@Path("")
public class CategorizationResource {
    private final Categorization categorization;
    private final LastSyncDAO lastSyncDAO;
    private final KeyWordsGetter keyWordsGetter;
    private final Syncer syncer;
    private final CategoryDAO categoryDao;

    @Autowired
    public CategorizationResource(Categorization categorization, LastSyncDAO lastSyncDAO, KeyWordsGetter keyWordsGetter, Syncer syncer, CategoryDAO categoryDao) {
        this.categorization = categorization;
        this.lastSyncDAO = lastSyncDAO;
        this.keyWordsGetter = keyWordsGetter;
        this.syncer = syncer;
        this.categoryDao = categoryDao;
    }

    @POST
    @Path("/categorize")
    @Consumes(MediaType.APPLICATION_OCTET_STREAM)
    @Produces(MediaType.APPLICATION_JSON)
    public String categorize( byte[] bytes) throws IOException, SQLException {
        String text = new String(bytes, StandardCharsets.UTF_8);
        text = text.replace("text=", "");
        text = text.replaceAll("\\+", " ");

        Category category = categorization.categorize(text);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("categoryId", category.getId());
        jsonObject.put("categoryName", category.getName());
        return jsonObject.toString();
    }

    /*@GET
    @Path("/keyWords")
    @Produces(MediaType.APPLICATION_JSON)
    public String getKeyWords() throws SQLException {
        List<String> keyWords = keyWordsGetter.getKeyWords();

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("keyWords", keyWords);
        return jsonObject.toString();
    }*/

    @GET
    @Path("/keywords-per-categories")
    @Produces(MediaType.APPLICATION_JSON)
    public String getKeyWordsPerCategories() throws SQLException {
        Map<Integer,List<String>> keyWordsPerCat = keyWordsGetter.getKeyWordsPerCat();

        List<JSONObject> jsons = new ArrayList<>();
        keyWordsPerCat.entrySet().forEach(es -> {
            JSONObject json = new JSONObject();
            json.put("categoryId", es.getKey());
            json.put("keyWords", es.getValue());
            jsons.add(json);
        });

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("keyWordsPerCategories", jsons);
        return jsonObject.toString();
    }

    @GET
    @Path("/categories")
    @Produces(MediaType.APPLICATION_JSON)
    public String getCategories() throws SQLException {
        List<JSONObject> categoriesJsons = new ArrayList<>();

        categoryDao.getCategories().stream().forEach(c -> {
            JSONObject json = new JSONObject();
            json.put("id", c.getId());
            json.put("name", c.getName());
            categoriesJsons.add(json);
        });

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("categories", categoriesJsons);
        return jsonObject.toString();
    }

    @GET
    @Path("/lastSync")
    @Produces(MediaType.APPLICATION_JSON)
    public Date getLastSync() {
        Date lastSync = lastSyncDAO.getLastSync();
        if (lastSync == null) {
            return Date.from(Instant.EPOCH);
        }

        return lastSync;
    }

    @POST
    @Path("/syncData")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response sync(String json) {
        JSONObject data = new JSONObject(json);
        if (Syncer.isSyncReady()) {
            syncer.sync(data);
            return Response.ok().build();
        }

        return Response.status(503).entity("Sync is not ready now.").build();
    }

}
