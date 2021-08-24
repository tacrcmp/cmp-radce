package dp.services;

import dp.dao.CategoryDAO;
import dp.dao.DocumentDAO;
import dp.dto.Category;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
public class Exporter {
    private static AtomicBoolean exportReady = new AtomicBoolean(true);
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private final CategoryDAO categoryDAO;
    private final DocumentDAO documentDAO;

    private static final Logger LOGGER = LoggerFactory.getLogger(Exporter.class);


    @Autowired
    public Exporter(CategoryDAO categoryDAO,
                  DocumentDAO documentDAO) {
        this.categoryDAO = categoryDAO;
        this.documentDAO = documentDAO;
    }

    public static boolean isExportReady() {
        return exportReady.get();
    }

    public void export(JSONObject data) {
        scheduler.execute(() -> {
            try {
                exportReady.set(false);
                String exportDestination = data.getString("destination");

                LOGGER.info("Exporting to {} started.", exportDestination);

                // prepare empty export folder, delete previous if exists
                File exportFolder = new File(exportDestination);
                if (exportFolder.exists()) {
                    FileSystemUtils.deleteRecursively(exportFolder);
                }
                exportFolder.mkdir();

                // create subfolders for categories
                List<Category> categories = categoryDAO.getCategories();
                Map<Integer, File> categoryFolders = new HashMap<>();
                categories.forEach(category -> {
                    File categoryFolder = new File(exportFolder, category.getName());
                    categoryFolder.mkdir();
                    categoryFolders.put(category.getId(), categoryFolder);
                });

                // export all documents to their categories
                documentDAO.getDocuments().forEach(document -> {
                    File categoryFolder = categoryFolders.get(document.getCategoryId());
                    String fileName = String.format("%d_%s.txt", document.getId(), document.getSource());
                    File documentOutputFile = new File(categoryFolder, fileName);
                    try (PrintWriter out = new PrintWriter(documentOutputFile)) {
                        out.println(document.getText());
                    } catch (FileNotFoundException e) {
                        LOGGER.error("Unable to save the document {}", document, e);
                    }
                });
                LOGGER.info("Exporting to {} done.", exportDestination);
                exportReady.set(true);
            } catch (Throwable e) {
                LOGGER.error("Export failed", e);
                throw new RuntimeException(e);
            }
        });
    }
}
