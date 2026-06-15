package com.fakenews.dao;

import com.fakenews.connection.DBConnection;
import com.fakenews.model.FactCheck;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * FactCheckDAO - Database operations for FactCheck entity.
 */
public class FactCheckDAO {

    public int addFactCheck(FactCheck fc) {
        String sql = "INSERT INTO fact_checks (content_type, article_id, post_id, claim, verdict, explanation, reference_urls, checked_by) VALUES (?,?,?,?,?,?,?,?)";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, fc.getContentType());
            if (fc.getArticleId() > 0) ps.setInt(2, fc.getArticleId()); else ps.setNull(2, Types.INTEGER);
            if (fc.getPostId() > 0) ps.setInt(3, fc.getPostId()); else ps.setNull(3, Types.INTEGER);
            ps.setString(4, fc.getClaim());
            ps.setString(5, fc.getVerdict());
            ps.setString(6, fc.getExplanation());
            ps.setString(7, fc.getReferenceUrls());
            ps.setInt(8, fc.getCheckedBy());
            ps.executeUpdate();
            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) return keys.getInt(1);
        } catch (SQLException e) {
            System.err.println("[FactCheckDAO] Add error: " + e.getMessage());
        }
        return -1;
    }

    public List<FactCheck> getFactChecksByArticle(int articleId) {
        List<FactCheck> list = new ArrayList<>();
        String sql = "SELECT * FROM fact_checks WHERE article_id = ? ORDER BY checked_at DESC";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, articleId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapResultSet(rs));
        } catch (SQLException e) {
            System.err.println("[FactCheckDAO] Get by article error: " + e.getMessage());
        }
        return list;
    }

    public List<FactCheck> getFactChecksByPost(int postId) {
        List<FactCheck> list = new ArrayList<>();
        String sql = "SELECT * FROM fact_checks WHERE post_id = ? ORDER BY checked_at DESC";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, postId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapResultSet(rs));
        } catch (SQLException e) {
            System.err.println("[FactCheckDAO] Get by post error: " + e.getMessage());
        }
        return list;
    }

    public List<FactCheck> getAllFactChecks() {
        List<FactCheck> list = new ArrayList<>();
        String sql = "SELECT * FROM fact_checks ORDER BY checked_at DESC";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapResultSet(rs));
        } catch (SQLException e) {
            System.err.println("[FactCheckDAO] Get all error: " + e.getMessage());
        }
        return list;
    }

    public List<FactCheck> getFactChecksByVerdict(String verdict) {
        List<FactCheck> list = new ArrayList<>();
        String sql = "SELECT * FROM fact_checks WHERE verdict = ? ORDER BY checked_at DESC";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, verdict);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapResultSet(rs));
        } catch (SQLException e) {
            System.err.println("[FactCheckDAO] Get by verdict error: " + e.getMessage());
        }
        return list;
    }

    private FactCheck mapResultSet(ResultSet rs) throws SQLException {
        FactCheck fc = new FactCheck();
        fc.setFactCheckId(rs.getInt("fact_check_id"));
        fc.setContentType(rs.getString("content_type"));
        fc.setArticleId(rs.getInt("article_id"));
        fc.setPostId(rs.getInt("post_id"));
        fc.setClaim(rs.getString("claim"));
        fc.setVerdict(rs.getString("verdict"));
        fc.setExplanation(rs.getString("explanation"));
        fc.setReferenceUrls(rs.getString("reference_urls"));
        fc.setCheckedBy(rs.getInt("checked_by"));
        Timestamp ts = rs.getTimestamp("checked_at");
        if (ts != null) fc.setCheckedAt(ts.toLocalDateTime());
        return fc;
    }
}