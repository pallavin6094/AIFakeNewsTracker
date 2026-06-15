package com.fakenews.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * SocialPost Model - Represents a social media post submitted for analysis.
 */
public class SocialPost {
    private int postId;
    private String platform;
    private String content;
    private String author;
    private String postUrl;
    private LocalDate postDate;
    private int submittedBy;
    private LocalDateTime submittedAt;
    private boolean deleted;

    public SocialPost() {}

    // Getters & Setters
    public int getPostId()                         { return postId; }
    public void setPostId(int postId)              { this.postId = postId; }
    public String getPlatform()                    { return platform; }
    public void setPlatform(String platform)       { this.platform = platform; }
    public String getContent()                     { return content; }
    public void setContent(String content)         { this.content = content; }
    public String getAuthor()                      { return author; }
    public void setAuthor(String author)           { this.author = author; }
    public String getPostUrl()                     { return postUrl; }
    public void setPostUrl(String postUrl)         { this.postUrl = postUrl; }
    public LocalDate getPostDate()                 { return postDate; }
    public void setPostDate(LocalDate postDate)    { this.postDate = postDate; }
    public int getSubmittedBy()                    { return submittedBy; }
    public void setSubmittedBy(int submittedBy)    { this.submittedBy = submittedBy; }
    public LocalDateTime getSubmittedAt()          { return submittedAt; }
    public void setSubmittedAt(LocalDateTime t)    { this.submittedAt = t; }
    public boolean isDeleted()                     { return deleted; }
    public void setDeleted(boolean deleted)        { this.deleted = deleted; }

    @Override
    public String toString() {
        return String.format("Post[ID=%d, Platform=%s, Author=%s, Date=%s]",
                postId, platform, author, postDate);
    }
}