package com.fakenews.dao;

import com.fakenews.connection.DBConnection;
import com.fakenews.model.CredibilityAnalysis;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * AnalysisDAO - Database operations for CredibilityAnalysis entity.
 */
public class AnalysisDAO {

    public int addAnalysis(CredibilityAnalysis analysis) {
        String sql = "INSERT INTO credibility_analysis (content_type, article_id, post_id, content_authenticity_score, fact_consistency_score, source_reliability_score, viral_risk_score, final_credibility_score, classification, analysis_notes, analyzed_by) VALUES (?,?,?,?,?,?,?,?,?,?,?)";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, analysis.getContentType());
            if (analysis.getArticleId() > 0) ps.setInt(2, analysis.getArticleId()); else ps.setNull(2, Types.INTEGER);
            if (analysis.getPostId() > 0) ps.setInt(3, analysis.getPostId()); else ps.setNull(3, Types.INTEGER);
            ps.setDouble(4, analysis.getContentAuthenticityScore());
            ps.setDouble(5, analysis.getFactConsistencyScore());
            ps.setDouble(6, analysis.getSourceReliabilityScore());
            ps.setDouble(7, analysis.getViralRiskScore());
            ps.setDouble(8, analysis.getFinalCredibilityScore());
            ps.setString(9, analysis.getClassification());
            ps.setString(10, analysis.getAnalysisNotes());
            if (analysis.getAnalyzedBy() > 0) ps.setInt(11, analysis.getAnalyzedBy()); else ps.setNull(11, Types.INTEGER);
            ps.executeUpdate();
            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) return keys.getInt(1);
        } catch (SQLException e) {
            System.err.println("[AnalysisDAO] Add error: " + e.getMessage());
        }
        return -1;
    }

    public CredibilityAnalysis getAnalysisByArticleId(int articleId) {
        String sql = "SELECT * FROM credibility_analysis WHERE article_id = ? ORDER BY analyzed_at DESC LIMIT 1";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, articleId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapResultSet(rs);
        } catch (SQLException e) {
            System.err.println("[AnalysisDAO] Get by article error: " + e.getMessage());
        }
        return null;
    }

    public CredibilityAnalysis getAnalysisByPostId(int postId) {
        String sql = "SELECT * FROM credibility_analysis WHERE post_id = ? ORDER BY analyzed_at DESC LIMIT 1";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, postId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapResultSet(rs);
        } catch (SQLException e) {
            System.err.println("[AnalysisDAO] Get by post error: " + e.getMessage());
        }
        return null;
    }

    public List<CredibilityAnalysis> getAllAnalyses() {
        List<CredibilityAnalysis> list = new ArrayList<>();
        String sql = "SELECT * FROM credibility_analysis ORDER BY analyzed_at DESC";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapResultSet(rs));
        } catch (SQLException e) {
            System.err.println("[AnalysisDAO] Get all error: " + e.getMessage());
        }
        return list;
    }

    public List<CredibilityAnalysis> getAnalysesByClassification(String classification) {
        List<CredibilityAnalysis> list = new ArrayList<>();
        String sql = "SELECT * FROM credibility_analysis WHERE classification = ? ORDER BY analyzed_at DESC";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, classification);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapResultSet(rs));
        } catch (SQLException e) {
            System.err.println("[AnalysisDAO] Get by classification error: " + e.getMessage());
        }
        return list;
    }

    public int getFakeNewsCount() {
        String sql = "SELECT COUNT(*) FROM credibility_analysis WHERE classification = 'Fake News'";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            System.err.println("[AnalysisDAO] Fake news count error: " + e.getMessage());
        }
        return 0;
    }

    public int getHighRiskCount() {
        String sql = "SELECT COUNT(*) FROM credibility_analysis WHERE classification IN ('Fake News','Misinformation Risk')";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            System.err.println("[AnalysisDAO] High risk count error: " + e.getMessage());
        }
        return 0;
    }

    private CredibilityAnalysis mapResultSet(ResultSet rs) throws SQLException {
        CredibilityAnalysis a = new CredibilityAnalysis();
        a.setAnalysisId(rs.getInt("analysis_id"));
        a.setContentType(rs.getString("content_type"));
        a.setArticleId(rs.getInt("article_id"));
        a.setPostId(rs.getInt("post_id"));
        a.setContentAuthenticityScore(rs.getDouble("content_authenticity_score"));
        a.setFactConsistencyScore(rs.getDouble("fact_consistency_score"));
        a.setSourceReliabilityScore(rs.getDouble("source_reliability_score"));
        a.setViralRiskScore(rs.getDouble("viral_risk_score"));
        a.setFinalCredibilityScore(rs.getDouble("final_credibility_score"));
        a.setClassification(rs.getString("classification"));
        a.setAnalysisNotes(rs.getString("analysis_notes"));
        a.setAnalyzedBy(rs.getInt("analyzed_by"));
        Timestamp ts = rs.getTimestamp("analyzed_at");
        if (ts != null) a.setAnalyzedAt(ts.toLocalDateTime());
        return a;
    }
}