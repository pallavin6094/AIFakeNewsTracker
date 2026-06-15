package com.fakenews.model;
 
import java.time.LocalDate;
import java.time.LocalDateTime;
 
/**
 * NewsArticle Model - Represents a news article submitted for analysis.
 */
public class NewsArticle {
    private int articleId;
    private String title;
    private String content;
    private String author;
    private int sourceId;
    private String sourceName;
    private String sourceUrl;
    private String category;
    private LocalDate publicationDate;
    private int submittedBy;
    private LocalDateTime submittedAt;
    private boolean deleted;
 
    public NewsArticle() {}
 
    // Getters & Setters
    public int getArticleId()                          { return articleId; }
    public void setArticleId(int articleId)            { this.articleId = articleId; }
    public String getTitle()                           { return title; }
    public void setTitle(String title)                 { this.title = title; }
    public String getContent()                         { return content; }
    public void setContent(String content)             { this.content = content; }
    public String getAuthor()                          { return author; }
    public void setAuthor(String author)               { this.author = author; }
    public int getSourceId()                           { return sourceId; }
    public void setSourceId(int sourceId)              { this.sourceId = sourceId; }
    public String getSourceName()                      { return sourceName; }
    public void setSourceName(String sourceName)       { this.sourceName = sourceName; }
    public String getSourceUrl()                       { return sourceUrl; }
    public void setSourceUrl(String sourceUrl)         { this.sourceUrl = sourceUrl; }
    public String getCategory()                        { return category; }
    public void setCategory(String category)           { this.category = category; }
    public LocalDate getPublicationDate()              { return publicationDate; }
    public void setPublicationDate(LocalDate d)        { this.publicationDate = d; }
    public int getSubmittedBy()                        { return submittedBy; }
    public void setSubmittedBy(int submittedBy)        { this.submittedBy = submittedBy; }
    public LocalDateTime getSubmittedAt()              { return submittedAt; }
    public void setSubmittedAt(LocalDateTime t)        { this.submittedAt = t; }
    public boolean isDeleted()                         { return deleted; }
    public void setDeleted(boolean deleted)            { this.deleted = deleted; }
 
    @Override
    public String toString() {
        return String.format("Article[ID=%d, Title=%s, Source=%s, Category=%s, Date=%s]",
                articleId, title, sourceName, category, publicationDate);
    }
}
