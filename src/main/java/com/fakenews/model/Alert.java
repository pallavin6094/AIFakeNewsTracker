package com.fakenews.model;

import java.time.LocalDateTime;

/**
 * Alert Model - Represents a system-generated alert for fake/misinfo content.
 */
public class Alert {
    private int alertId;
    private String contentType;
    private int articleId;
    private int postId;
    private String alertType;
    private String alertLevel;    // Low / Medium / High / Critical
    private String alertMessage;
    private boolean resolved;
    private int resolvedBy;
    private LocalDateTime resolvedAt;
    private LocalDateTime createdAt;

    public Alert() {}

    public int getAlertId()                        { return alertId; }
    public void setAlertId(int alertId)            { this.alertId = alertId; }
    public String getContentType()                 { return contentType; }
    public void setContentType(String t)           { this.contentType = t; }
    public int getArticleId()                      { return articleId; }
    public void setArticleId(int articleId)        { this.articleId = articleId; }
    public int getPostId()                         { return postId; }
    public void setPostId(int postId)              { this.postId = postId; }
    public String getAlertType()                   { return alertType; }
    public void setAlertType(String alertType)     { this.alertType = alertType; }
    public String getAlertLevel()                  { return alertLevel; }
    public void setAlertLevel(String alertLevel)   { this.alertLevel = alertLevel; }
    public String getAlertMessage()                { return alertMessage; }
    public void setAlertMessage(String msg)        { this.alertMessage = msg; }
    public boolean isResolved()                    { return resolved; }
    public void setResolved(boolean resolved)      { this.resolved = resolved; }
    public int getResolvedBy()                     { return resolvedBy; }
    public void setResolvedBy(int resolvedBy)      { this.resolvedBy = resolvedBy; }
    public LocalDateTime getResolvedAt()           { return resolvedAt; }
    public void setResolvedAt(LocalDateTime t)     { this.resolvedAt = t; }
    public LocalDateTime getCreatedAt()            { return createdAt; }
    public void setCreatedAt(LocalDateTime t)      { this.createdAt = t; }

    @Override
    public String toString() {
        return String.format("Alert[ID=%d, Level=%s, Type=%s, Resolved=%b]",
                alertId, alertLevel, alertType, resolved);
    }
}