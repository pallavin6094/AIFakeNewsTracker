package com.fakenews.service;

import com.fakenews.dao.AnalysisDAO;
import com.fakenews.dao.AlertDAO;
import com.fakenews.dao.AuditLogDAO;
import com.fakenews.model.*;
import com.fakenews.util.AIAnalysisUtil;

/**
 * AnalysisService - Orchestrates AI credibility analysis and auto-alert generation.
 */
public class AnalysisService {

    private final AnalysisDAO  analysisDAO  = new AnalysisDAO();
    private final AlertDAO     alertDAO     = new AlertDAO();
    private final AuditLogDAO  auditLogDAO  = new AuditLogDAO();

    /**
     * Runs AI analysis on a NewsArticle, stores result, and auto-generates alerts if needed.
     * Returns the saved CredibilityAnalysis or null on failure.
     */
    public CredibilityAnalysis analyzeArticle(NewsArticle article, User user) {
        CredibilityAnalysis analysis = AIAnalysisUtil.analyzeArticle(article, user.getUserId());

        int id = analysisDAO.addAnalysis(analysis);
        if (id < 0) {
            System.out.println("  [!] Failed to save analysis result.");
            return null;
        }
        analysis.setAnalysisId(id);

        // Auto-generate alert for suspicious/fake content
        if (analysis.getFinalCredibilityScore() <= 60) {
            generateAlert("Article", article.getArticleId(), 0, analysis);
        }

        auditLogDAO.addLog(new AuditLog("User", user.getUserId(), user.getUsername(),
                "ANALYSIS", "Analyzed article ID: " + article.getArticleId()));
        return analysis;
    }

    /**
     * Runs AI analysis on a SocialPost, stores result, and auto-generates alerts if needed.
     */
    public CredibilityAnalysis analyzePost(SocialPost post, User user) {
        CredibilityAnalysis analysis = AIAnalysisUtil.analyzePost(post, user.getUserId());

        int id = analysisDAO.addAnalysis(analysis);
        if (id < 0) {
            System.out.println("  [!] Failed to save analysis result.");
            return null;
        }
        analysis.setAnalysisId(id);

        if (analysis.getFinalCredibilityScore() <= 60) {
            generateAlert("Post", 0, post.getPostId(), analysis);
        }

        auditLogDAO.addLog(new AuditLog("User", user.getUserId(), user.getUsername(),
                "ANALYSIS", "Analyzed post ID: " + post.getPostId()));
        return analysis;
    }

    /**
     * Creates and persists an alert based on analysis result.
     */
    private void generateAlert(String contentType, int articleId, int postId, CredibilityAnalysis analysis) {
        Alert alert = new Alert();
        alert.setContentType(contentType);
        alert.setArticleId(articleId);
        alert.setPostId(postId);
        alert.setAlertLevel(AIAnalysisUtil.getAlertLevel(analysis.getFinalCredibilityScore()));
        alert.setAlertType("Fake News Detected");
        alert.setAlertMessage(String.format(
                "Content classified as '%s' with credibility score %.1f/100. Immediate review recommended.",
                analysis.getClassification(), analysis.getFinalCredibilityScore()));
        alertDAO.addAlert(alert);
    }

    /** Retrieves the latest analysis for a given article. */
    public CredibilityAnalysis getArticleAnalysis(int articleId) {
        return analysisDAO.getAnalysisByArticleId(articleId);
    }

    /** Retrieves the latest analysis for a given post. */
    public CredibilityAnalysis getPostAnalysis(int postId) {
        return analysisDAO.getAnalysisByPostId(postId);
    }

    /** Prints a formatted analysis result to the console. */
    public void printAnalysisResult(CredibilityAnalysis a) {
        System.out.println("\n  ┌─────────────────────────────────────────────────┐");
        System.out.println("  │           AI CREDIBILITY ANALYSIS RESULT        │");
        System.out.println("  ├─────────────────────────────────────────────────┤");
        System.out.printf("  │  Content Authenticity Score : %6.1f / 100      │%n", a.getContentAuthenticityScore());
        System.out.printf("  │  Fact Consistency Score     : %6.1f / 100      │%n", a.getFactConsistencyScore());
        System.out.printf("  │  Source Reliability Score   : %6.1f / 100      │%n", a.getSourceReliabilityScore());
        System.out.printf("  │  Viral Risk Score           : %6.1f / 100      │%n", a.getViralRiskScore());
        System.out.println("  ├─────────────────────────────────────────────────┤");
        System.out.printf("  │  ★ FINAL CREDIBILITY SCORE  : %6.1f / 100      │%n", a.getFinalCredibilityScore());
        System.out.printf("  │  ★ CLASSIFICATION           : %-20s │%n", a.getClassification());
        System.out.println("  └─────────────────────────────────────────────────┘");
        if (a.getAnalysisNotes() != null) {
            System.out.println("\n  Analysis Notes:");
            System.out.println("  " + a.getAnalysisNotes().replace("\n", "\n  "));
        }
    }
}
