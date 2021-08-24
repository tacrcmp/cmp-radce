package dp.dto;

public class Document {
    private Integer id;
    private String source;
    private String text;
    private Integer categoryId;
    private Category category;

    public Document(Integer id, String source, String text, Integer categoryId) {
        this.id = id;
        this.text = text;
        this.categoryId = categoryId;
        this.source = source;
    }

    public Document(String text, Integer categoryId) {
        this.text = text;
        this.categoryId = categoryId;
    }

    public Document(String text) {
        this.text = text;
    }

    public Document() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

}
