package com.fakenews.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * SpreadTracking Model - Tracks viral spread of news content.
 */
public class SpreadTracking {
    private int spreadId;
    private String contentType;
    private int articleId;
    private int postId;
    private String originalSource;
    private String repostedBy;
    private String platform;
    private LocalDate spreadDate;
    private int reachCount;
    private String spreadNotes;
    private int recordedBy;
    private LocalDateTime recordedAt;

    public SpreadTracking() {}

    public int getSpreadId()                           { return spreadId; }
    public void setSpreadId(int spreadId)              { this.spreadId = spreadId; }
    public String getContentType()                     { return contentType; }
    public void setContentType(String contentType)     { this.contentType = contentType; }
    public int getArticleId()                          { return articleId; }
    public void setArticleId(int articleId)            { this.articleId = articleId; }
    public int getPostId()                             { return postId; }
    public void setPostId(int postId)                  { this.postId = postId; }
    public String getOriginalSource()                  { return originalSource; }
    public void setOriginalSource(String src)          { this.originalSource = src; }
    public String getRepostedBy()                      { return repostedBy; }
    public void setRepostedBy(String by)               { this.repostedBy = by; }
    public String getPlatform()                        { return platform; }
    public void setPlatform(String platform)           { this.platform = platform; }
    public LocalDate getSpreadDate()                   { return spreadDate; }
    public void setSpreadDate(LocalDate spreadDate)    { this.spreadDate = spreadDate; }
    public int getReachCount()                         { return reachCount; }
    public void setReachCount(int reachCount)          { this.reachCount = reachCount; }
    public String getSpreadNotes()                     { return spreadNotes; }
    public void setSpreadNotes(String notes)           { this.spreadNotes = notes; }
    public int getRecordedBy()                         { return recordedBy; }
    public void setRecordedBy(int recordedBy)          { this.recordedBy = recordedBy; }
    public LocalDateTime getRecordedAt()               { return recordedAt; }
    public void setRecordedAt(LocalDateTime t)         { this.recordedAt = t; }

    @Override
    public String toString() {
        return String.format("Spread[ID=%d, Platform=%s, Reach=%d, Date=%s]",
                spreadId, platform, reachCount, spreadDate);
    }
}