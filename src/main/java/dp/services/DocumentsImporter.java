package dp.services;

import com.google.common.collect.Lists;
import dp.dao.CategoryDAO;
import dp.dao.DocumentDAO;
import dp.dto.Category;
import dp.dto.Document;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.List;

@Service
public class DocumentsImporter {
    private final DocumentDAO documentDAO;
    private final CategoryDAO categoryDAO;
    private File lastDir;

    @Autowired
    public DocumentsImporter(CategoryDAO categoryDAO, DocumentDAO documentDAO) {
        this.documentDAO = documentDAO;
        this.categoryDAO = categoryDAO;
    }

    public void importTexts(String path) throws IOException {
        File root = new File(path);

        if (root.exists()) {
            this.lastDir = root;

            if (root.isDirectory()) {
                processDir(root);
            } else {
                processFile(root);
            }
        }
    }

    public Document importDocument(String path) throws IOException {
        File file = new File(path);
        String categoryName = file.getParentFile().getName();
        Integer categoryId = categoryDAO.getCategoryId(categoryName);

        Document document = new Document();
        document.setText(getDocText(file));
        document.setCategoryId(categoryId);

        return document;
    }

    private void processDir(File dir) throws IOException {
        File[] files = dir.listFiles();
        this.lastDir = dir;

        if (files != null) {
            for (File file : Lists.newArrayList(files)) {
                if (file.isDirectory()) {
                    processDir(file);
                } else {
                    processFile(file);
                }
            }
        } else {
            throw new IllegalArgumentException("Dir " + dir.getAbsolutePath() + " is empty.");
        }
    }

    private void processFile(File file) throws IOException {
        Document document = new Document();
        String text = getDocText(file);
        document.setText(text);
        document.setSource("local");

        String categoryName = lastDir.getName();
        Integer categoryId = categoryDAO.getCategoryId(categoryName);
        if (categoryId == -1) {
            categoryId = categoryDAO.putCategory(new Category(categoryName));
        }

        document.setCategoryId(categoryId);
        documentDAO.putDocument(document);
    }

    private String readDocx(File file) {
        StringBuilder builder = new StringBuilder();
        try {
            FileInputStream fis = new FileInputStream(file.getAbsolutePath());
            XWPFDocument xwpfDocument = new XWPFDocument(fis);
            List<XWPFParagraph> paragraphs = xwpfDocument.getParagraphs();
            for (XWPFParagraph para : paragraphs) {
                String text = para.getText();

                if (!text.isEmpty()) {
                    builder.append(text).append(" ");
                }
            }
            fis.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return builder.toString();
    }

    private String readTxt(File file) throws IOException {
        StringBuilder builder = new StringBuilder();

        BufferedReader in = new BufferedReader(new FileReader(file));
        String line;
        while ((line = in.readLine()) != null) {
            builder.append(line).append(" ");
        }

        return builder.toString();
    }

    private String getDocText(File file) throws IOException {
        String extension = file.getName().substring(file.getName().lastIndexOf(".") + 1);
        switch (extension) {
            case "docx":
            case "doc":
                return readDocx(file);
            case "txt":
                return readTxt(file);
            default:
                throw new UnsupportedEncodingException();
        }
    }
}
