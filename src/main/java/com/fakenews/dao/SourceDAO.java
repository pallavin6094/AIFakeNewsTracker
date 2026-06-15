package com.fakenews.dao;

import com.fakenews.connection.DBConnection;
import com.fakenews.model.NewsSource;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * SourceDAO - Database operations for NewsSource entity.
 */
public class SourceDAO {

    public int addSource(NewsSource source) {
        String sql = "INSERT INTO news_sources (source_name, source_url, reliability_level, reliability_score, added_by_admin) VALUES (?,?,?,?,?)";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, source.getSourceName());
            ps.setString(2, source.getSourceUrl());
            ps.setString(3, source.getReliabilityLevel());
            ps.setDouble(4, source.getReliabilityScore());
            ps.setInt(5, source.getAddedByAdmin());
            ps.executeUpdate();
            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) return keys.getInt(1);
        } catch (SQLException e) {
            System.err.println("[SourceDAO] Add error: " + e.getMessage());
        }
        return -1;
    }

    public boolean updateSource(NewsSource source) {
        String sql = "UPDATE news_sources SET source_name=?, source_url=?, reliability_level=?, reliability_score=? WHERE source_id=?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, source.getSourceName());
            ps.setString(2, source.getSourceUrl());
            ps.setString(3, source.getReliabilityLevel());
            ps.setDouble(4, source.getReliabilityScore());
            ps.setInt(5, source.getSourceId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[SourceDAO] Update error: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteSource(int sourceId) {
        String sql = "DELETE FROM news_sources WHERE source_id = ?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, sourceId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[SourceDAO] Delete error: " + e.getMessage());
            return false;
        }
    }

    public NewsSource getSourceById(int id) {
        String sql = "SELECT * FROM news_sources WHERE source_id = ?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapResultSet(rs);
        } catch (SQLException e) {
            System.err.println("[SourceDAO] Get error: " + e.getMessage());
        }
        return null;
    }

    public List<NewsSource> getAllSources() {
        List<NewsSource> list = new ArrayList<>();
        String sql = "SELECT * FROM news_sources ORDER BY reliability_score DESC";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapResultSet(rs));
        } catch (SQLException e) {
            System.err.println("[SourceDAO] Get all error: " + e.getMessage());
        }
        return list;
    }

    /**
     * Recalculates source reliability score based on history.
     * Formula: 100 - (misinfoCount / totalArticles * 100) * 0.8
     */
    public boolean recalculateReliability(int sourceId) {
        String getSql = "SELECT total_articles, misinfo_count FROM news_sources WHERE source_id = ?";
        String updateSql = "UPDATE news_sources SET reliability_score = ?, reliability_level = ? WHERE source_id = ?";
        try {
            double score = 50.0;
            int total = 0, misinfo = 0;
            try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(getSql)) {
                ps.setInt(1, sourceId);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) { total = rs.getInt("total_articles"); misinfo = rs.getInt("misinfo_count"); }
            }
            if (total > 0) {
                score = 100.0 - ((double) misinfo / total * 100.0) * 0.8;
                score = Math.max(0, Math.min(100, score));
            }
            String level = score >= 85 ? "Very Reliable" : score >= 70 ? "Reliable" :
                           score >= 50 ? "Moderate" : score >= 30 ? "Unverified" : "Unreliable";
            try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(updateSql)) {
                ps.setDouble(1, score); ps.setString(2, level); ps.setInt(3, sourceId);
                return ps.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.err.println("[SourceDAO] Recalculate error: " + e.getMessage());
            return false;
        }
    }

    private NewsSource mapResultSet(ResultSet rs) throws SQLException {
        NewsSource s = new NewsSource();
        s.setSourceId(rs.getInt("source_id"));
        s.setSourceName(rs.getString("source_name"));
        s.setSourceUrl(rs.getString("source_url"));
        s.setReliabilityLevel(rs.getString("reliability_level"));
        s.setReliabilityScore(rs.getDouble("reliability_score"));
        s.setTotalArticles(rs.getInt("total_articles"));
        s.setMisinfoCount(rs.getInt("misinfo_count"));
        s.setFactCheckCount(rs.getInt("fact_check_count"));
        s.setAddedByAdmin(rs.getInt("added_by_admin"));
        Timestamp ct = rs.getTimestamp("created_at");
        if (ct != null) s.setCreatedAt(ct.toLocalDateTime());
        return s;
    }
}
