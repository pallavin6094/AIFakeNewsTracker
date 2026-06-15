package com.fakenews.model;

import java.time.LocalDateTime;

/**
 * NewsSource Model - Represents a news source and its reliability rating.
 */
public class NewsSource {
    private int sourceId;
    private String sourceName;
    private String sourceUrl;
    private String reliabilityLevel;
    private double reliabilityScore;
    private int totalArticles;
    private int misinfoCount;
    private int factCheckCount;
    private int addedByAdmin;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public NewsSource() {}

    // Getters & Setters
    public int getSourceId()                             { return sourceId; }
    public void setSourceId(int sourceId)                { this.sourceId = sourceId; }
    public String getSourceName()                        { return sourceName; }
    public void setSourceName(String sourceName)         { this.sourceName = sourceName; }
    public String getSourceUrl()                         { return sourceUrl; }
    public void setSourceUrl(String sourceUrl)           { this.sourceUrl = sourceUrl; }
    public String getReliabilityLevel()                  { return reliabilityLevel; }
    public void setReliabilityLevel(String level)        { this.reliabilityLevel = level; }
    public double getReliabilityScore()                  { return reliabilityScore; }
    public void setReliabilityScore(double score)        { this.reliabilityScore = score; }
    public int getTotalArticles()                        { return totalArticles; }
    public void setTotalArticles(int totalArticles)      { this.totalArticles = totalArticles; }
    public int getMisinfoCount()                         { return misinfoCount; }
    public void setMisinfoCount(int misinfoCount)        { this.misinfoCount = misinfoCount; }
    public int getFactCheckCount()                       { return factCheckCount; }
    public void setFactCheckCount(int factCheckCount)    { this.factCheckCount = factCheckCount; }
    public int getAddedByAdmin()                         { return addedByAdmin; }
    public void setAddedByAdmin(int addedByAdmin)        { this.addedByAdmin = addedByAdmin; }
    public LocalDateTime getCreatedAt()                  { return createdAt; }
    public void setCreatedAt(LocalDateTime t)            { this.createdAt = t; }
    public LocalDateTime getUpdatedAt()                  { return updatedAt; }
    public void setUpdatedAt(LocalDateTime t)            { this.updatedAt = t; }

    @Override
    public String toString() {
        return String.format("Source[ID=%d, Name=%s, Level=%s, Score=%.1f]",
                sourceId, sourceName, reliabilityLevel, reliabilityScore);
    }
}