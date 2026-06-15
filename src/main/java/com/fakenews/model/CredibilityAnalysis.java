package com.fakenews.model;

import java.time.LocalDateTime;

/**
 * CredibilityAnalysis Model - Stores AI credibility scoring results.
 */
public class CredibilityAnalysis {
    private int analysisId;
    private String contentType;       // "Article" or "Post"
    private int articleId;
    private int postId;
    private double contentAuthenticityScore;
    private double factConsistencyScore;
    private double sourceReliabilityScore;
    private double viralRiskScore;
    private double finalCredibilityScore;
    private String classification;
    private String analysisNotes;
    private LocalDateTime analyzedAt;
    private int analyzedBy;

    public CredibilityAnalysis() {}

    // Getters & Setters
    public int getAnalysisId()                               { return analysisId; }
    public void setAnalysisId(int analysisId)                { this.analysisId = analysisId; }
    public String getContentType()                           { return contentType; }
    public void setContentType(String contentType)           { this.contentType = contentType; }
    public int getArticleId()                                { return articleId; }
    public void setArticleId(int articleId)                  { this.articleId = articleId; }
    public int getPostId()                                   { return postId; }
    public void setPostId(int postId)                        { this.postId = postId; }
    public double getContentAuthenticityScore()              { return contentAuthenticityScore; }
    public void setContentAuthenticityScore(double s)        { this.contentAuthenticityScore = s; }
    public double getFactConsistencyScore()                  { return factConsistencyScore; }
    public void setFactConsistencyScore(double s)            { this.factConsistencyScore = s; }
    public double getSourceReliabilityScore()                { return sourceReliabilityScore; }
    public void setSourceReliabilityScore(double s)          { this.sourceReliabilityScore = s; }
    public double getViralRiskScore()                        { return viralRiskScore; }
    public void setViralRiskScore(double s)                  { this.viralRiskScore = s; }
    public double getFinalCredibilityScore()                 { return finalCredibilityScore; }
    public void setFinalCredibilityScore(double s)           { this.finalCredibilityScore = s; }
    public String getClassification()                        { return classification; }
    public void setClassification(String classification)     { this.classification = classification; }
    public String getAnalysisNotes()                         { return analysisNotes; }
    public void setAnalysisNotes(String notes)               { this.analysisNotes = notes; }
    public LocalDateTime getAnalyzedAt()                     { return analyzedAt; }
    public void setAnalyzedAt(LocalDateTime t)               { this.analyzedAt = t; }
    public int getAnalyzedBy()                               { return analyzedBy; }
    public void setAnalyzedBy(int analyzedBy)                { this.analyzedBy = analyzedBy; }

    @Override
    public String toString() {
        return String.format("Analysis[ID=%d, Type=%s, Score=%.1f, Class=%s]",
                analysisId, contentType, finalCredibilityScore, classification);
    }
}
