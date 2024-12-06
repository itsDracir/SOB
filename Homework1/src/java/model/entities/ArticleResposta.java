package model.entities;

import java.util.Date;

/**
 *
 * Clase auxiliar para estructurar respuestas relacionadas con artículos.
 */
public class ArticleResposta {

    private String title;
    private String authorName;
    private String topics;
    private Date publicationDate;
    private Integer viewCount;
    private String link;

    public ArticleResposta() {}

    public ArticleResposta(String title, String authorName, String topics, Date publicationDate, Integer viewCount, String link) {
        this.title = title;
        this.authorName = authorName;
        this.topics = topics;
        this.publicationDate = publicationDate;
        this.viewCount = viewCount;
        this.link = link;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getTopics() {
        return topics;
    }

    public void setTopics(String topics) {
        this.topics = topics;
    }

    public Date getPublicationDate() {
        return publicationDate;
    }

    public void setPublicationDate(Date publicationDate) {
        this.publicationDate = publicationDate;
    }

    public Integer getViewCount() {
        return viewCount;
    }

    public void setViewCount(Integer viewCount) {
        this.viewCount = viewCount;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
}
