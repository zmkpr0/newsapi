package pro.pham.spring.newsapi.entity;


import java.util.*;

public class News {

    private Long id;
    private String title;
    private String content;
    private List<String> tags = new ArrayList<>();
    private Date date;

    public News() {

    }

    public News(Long id, String title, String content, Date date) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.date = date;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public List<String> getTags() {
        return Collections.unmodifiableList(tags);
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public void addTag(String tag) {
        tags.add(tag);
    }
}
