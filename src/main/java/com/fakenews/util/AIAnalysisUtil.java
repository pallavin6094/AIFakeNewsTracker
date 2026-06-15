package com.fakenews.util;

import com.fakenews.model.CredibilityAnalysis;
import com.fakenews.model.NewsArticle;
import com.fakenews.model.SocialPost;

import java.util.Arrays;
import java.util.List;

/**
 * AIAnalysisUtil - Simulates AI-based credibility scoring for articles and posts.
 *
 * Scoring logic uses keyword detection, source signals, content length,
 * and linguistic pattern matching to simulate real-world NLP analysis.
 *
 * Scores range 0–100:
 *   81-100 = Highly Credible
 *   61-80  = Credible
 *   41-60  = Suspicious
 *   21-40  = Misinformation Risk
 *   0-20   = Fake News
 */
public class AIAnalysisUtil {

    // Keywords that lower credibility score (misinformation signals)
    private static final List<String> FAKE_KEYWORDS = Arrays.asList(
        "breaking", "shocking", "secret", "banned", "cure", "miracle",
        "they don't want you to know", "share before deleted", "exposed",
        "100%", "guaranteed", "conspiracy", "hoax", "fake", "scam",
        "unbelievable", "you won't believe", "mainstream media hiding",
        "government hiding", "deep state", "plandemic", "crisis actor"
    );

    // Keywords that increase credibility score
    private static final List<String> CREDIBLE_KEYWORDS = Arrays.asList(
        "according to", "study shows", "research", "published", "official",
        "government", "university", "expert", "scientist", "journal",
        "report", "confirmed", "verified", "evidence", "data", "statistics",
        "peer-reviewed", "source", "announced", "stated"
    );

    // Reliable source domains
    private static final List<String> RELIABLE_DOMAINS = Arrays.asList(
        "reuters.com", "bbc.com", "apnews.com", "thehindu.com",
        "ndtv.com", "timesofindia.com", "who.int", "gov.in",
        "nature.com", "science.org", "pubmed.ncbi.nlm.nih.gov"
    );

    // Unreliable source patterns
    private static final List<String> UNRELIABLE_PATTERNS = Arrays.asList(
        "whatsapp", "forward", "viral", "chain", "unknown", "anonymous",
        "fakenews", "rumour", "rumor", "unverified"
    );

    /**
     * Analyzes a news article and returns a CredibilityAnalysis object.
     */
    public static CredibilityAnalysis analyzeArticle(NewsArticle article, int userId) {
        String combinedText = (article.getTitle() + " " + article.getContent()).toLowerCase();
        String sourceUrl = article.getSourceUrl() != null ? article.getSourceUrl().toLowerCase() : "";
        String sourceName = article.getSourceName() != null ? article.getSourceName().toLowerCase() : "";

        double contentScore  = calculateContentAuthenticityScore(combinedText, article.getContent().length());
        double factScore     = calculateFactConsistencyScore(combinedText);
        double sourceScore   = calculateSourceReliabilityScore(sourceUrl, sourceName);
        double viralScore    = calculateViralRiskScore(combinedText);

        // Weighted average: content 30%, fact 30%, source 25%, viral inverse 15%
        double finalScore = (contentScore * 0.30) + (factScore * 0.30) +
                            (sourceScore * 0.25) + ((100 - viralScore) * 0.15);
        finalScore = Math.round(finalScore * 10.0) / 10.0;
        finalScore = Math.max(0, Math.min(100, finalScore));

        CredibilityAnalysis analysis = new CredibilityAnalysis();
        analysis.setContentType("Article");
        analysis.setArticleId(article.getArticleId());
        analysis.setContentAuthenticityScore(contentScore);
        analysis.setFactConsistencyScore(factScore);
        analysis.setSourceReliabilityScore(sourceScore);
        analysis.setViralRiskScore(viralScore);
        analysis.setFinalCredibilityScore(finalScore);
        analysis.setClassification(classify(finalScore));
        analysis.setAnalysisNotes(generateNotes(combinedText, sourceUrl, finalScore));
        analysis.setAnalyzedBy(userId);
        return analysis;
    }

    /**
     * Analyzes a social media post and returns a CredibilityAnalysis object.
     */
    public static CredibilityAnalysis analyzePost(SocialPost post, int userId) {
        String combinedText = post.getContent().toLowerCase();
        String postUrl = post.getPostUrl() != null ? post.getPostUrl().toLowerCase() : "";

        double contentScore = calculateContentAuthenticityScore(combinedText, post.getContent().length());
        double factScore    = calculateFactConsistencyScore(combinedText);
        double sourceScore  = calculateSourceReliabilityScore(postUrl, post.getPlatform().toLowerCase());
        double viralScore   = calculateViralRiskScore(combinedText);

        // Social posts get slightly lower base source score
        sourceScore = Math.max(0, sourceScore - 10);

        double finalScore = (contentScore * 0.30) + (factScore * 0.30) +
                            (sourceScore * 0.25) + ((100 - viralScore) * 0.15);
        finalScore = Math.round(finalScore * 10.0) / 10.0;
        finalScore = Math.max(0, Math.min(100, finalScore));

        CredibilityAnalysis analysis = new CredibilityAnalysis();
        analysis.setContentType("Post");
        analysis.setPostId(post.getPostId());
        analysis.setContentAuthenticityScore(contentScore);
        analysis.setFactConsistencyScore(factScore);
        analysis.setSourceReliabilityScore(sourceScore);
        analysis.setViralRiskScore(viralScore);
        analysis.setFinalCredibilityScore(finalScore);
        analysis.setClassification(classify(finalScore));
        analysis.setAnalysisNotes(generateNotes(combinedText, postUrl, finalScore));
        analysis.setAnalyzedBy(userId);
        return analysis;
    }

    // ── Score Calculators ─────────────────────────────────────

    private static double calculateContentAuthenticityScore(String text, int length) {
        double score = 60.0;

        // Longer content with structure suggests authenticity
        if (length > 500)  score += 10;
        if (length > 1000) score += 10;

        // Credible keyword presence
        for (String kw : CREDIBLE_KEYWORDS) {
            if (text.contains(kw)) { score += 3; }
        }

        // Fake keyword presence reduces score
        for (String kw : FAKE_KEYWORDS) {
            if (text.contains(kw)) { score -= 6; }
        }

        // Excessive punctuation (clickbait signal)
        long exclamations = text.chars().filter(c -> c == '!').count();
        long questions    = text.chars().filter(c -> c == '?').count();
        if (exclamations > 3) score -= 8;
        if (questions > 5)    score -= 5;

        // ALL CAPS words (sensation signal)
        int capsWords = countCapsWords(text);
        if (capsWords > 3) score -= 10;

        return Math.max(0, Math.min(100, score));
    }

    private static double calculateFactConsistencyScore(String text) {
        double score = 55.0;
        int credibleMatches = 0;
        int fakeMatches     = 0;

        for (String kw : CREDIBLE_KEYWORDS) { if (text.contains(kw)) credibleMatches++; }
        for (String kw : FAKE_KEYWORDS)     { if (text.contains(kw)) fakeMatches++;     }

        score += (credibleMatches * 3.0);
        score -= (fakeMatches * 5.0);

        // Numbers/statistics suggest fact basis
        long numbers = text.chars().filter(Character::isDigit).count();
        if (numbers > 5) score += 5;

        return Math.max(0, Math.min(100, score));
    }

    private static double calculateSourceReliabilityScore(String sourceUrl, String sourceName) {
        double score = 50.0;

        for (String domain : RELIABLE_DOMAINS) {
            if (sourceUrl.contains(domain) || sourceName.contains(domain)) {
                score = 90.0;
                break;
            }
        }

        for (String pattern : UNRELIABLE_PATTERNS) {
            if (sourceUrl.contains(pattern) || sourceName.contains(pattern)) {
                score -= 25;
            }
        }

        if (sourceUrl.isEmpty() && sourceName.isEmpty()) score = 20.0;

        return Math.max(0, Math.min(100, score));
    }

    private static double calculateViralRiskScore(String text) {
        double score = 20.0;

        String[] viralPhrases = {"share this", "share now", "must share", "forward this",
                                  "tell everyone", "spread the word", "go viral", "retweet",
                                  "share before", "deleted soon"};
        for (String phrase : viralPhrases) {
            if (text.contains(phrase)) score += 15;
        }

        for (String kw : FAKE_KEYWORDS) {
            if (text.contains(kw)) score += 5;
        }

        return Math.max(0, Math.min(100, score));
    }

    // ── Classification ────────────────────────────────────────

    public static String classify(double score) {
        if (score >= 81) return "Highly Credible";
        if (score >= 61) return "Credible";
        if (score >= 41) return "Suspicious";
        if (score >= 21) return "Misinformation Risk";
        return "Fake News";
    }

    // ── Alert Level ───────────────────────────────────────────

    public static String getAlertLevel(double finalScore) {
        if (finalScore <= 20) return "Critical";
        if (finalScore <= 40) return "High";
        if (finalScore <= 60) return "Medium";
        return "Low";
    }

    // ── Helpers ───────────────────────────────────────────────

    private static int countCapsWords(String text) {
        int count = 0;
        for (String word : text.split("\\s+")) {
            if (word.length() > 2 && word.equals(word.toUpperCase())) count++;
        }
        return count;
    }

    private static String generateNotes(String text, String sourceUrl, double finalScore) {
        StringBuilder sb = new StringBuilder();
        sb.append("AI Analysis Summary:\n");
        sb.append(String.format("Final Score: %.1f | Classification: %s\n", finalScore, classify(finalScore)));

        int fakeCount = 0;
        for (String kw : FAKE_KEYWORDS) { if (text.contains(kw)) fakeCount++; }
        int credibleCount = 0;
        for (String kw : CREDIBLE_KEYWORDS) { if (text.contains(kw)) credibleCount++; }

        if (fakeCount > 0)     sb.append("- Detected ").append(fakeCount).append(" misinformation signal(s).\n");
        if (credibleCount > 0) sb.append("- Detected ").append(credibleCount).append(" credibility indicator(s).\n");
        if (sourceUrl.isEmpty()) sb.append("- No source URL provided (reduces reliability score).\n");
        if (finalScore < 40)   sb.append("- ALERT: Content flagged for review by moderators.\n");

        return sb.toString();
    }
}