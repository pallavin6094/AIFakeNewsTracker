package com.fakenews.dao;

import com.fakenews.connection.DBConnection;
import com.fakenews.model.Alert;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
public class AlertDAO {
	public int addAlert(Alert alert) {
        String sql = "INSERT INTO alerts (content_type, article_id, post_id, alert_type, alert_level, alert_message) VALUES (?,?,?,?,?,?)";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, alert.getContentType());
            if (alert.getArticleId() > 0) ps.setInt(2, alert.getArticleId()); else ps.setNull(2, Types.INTEGER);
            if (alert.getPostId() > 0) ps.setInt(3, alert.getPostId()); else ps.setNull(3, Types.INTEGER);
            ps.setString(4, alert.getAlertType());
            ps.setString(5, alert.getAlertLevel());
            ps.setString(6, alert.getAlertMessage());
            ps.executeUpdate();
            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) return keys.getInt(1);
        } catch (SQLException e) {
            System.err.println("[AlertDAO] Add error: " + e.getMessage());
        }
        return -1;
    }
	public boolean resolveAlert(int alertId, int adminId) {
        String sql = "UPDATE alerts SET is_resolved=1, resolved_by=?, resolved_at=NOW() WHERE alert_id=?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, adminId);
            ps.setInt(2, alertId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[AlertDAO] Resolve error: " + e.getMessage());
            return false;
        }
    }

    public List<Alert> getUnresolvedAlerts() {
        List<Alert> list = new ArrayList<>();
        String sql = "SELECT * FROM alerts WHERE is_resolved = 0 ORDER BY created_at DESC";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapResultSet(rs));
        } catch (SQLException e) {
            System.err.println("[AlertDAO] Get unresolved error: " + e.getMessage());
        }
        return list;
    }
    public List<Alert> getAllAlerts() {
        List<Alert> list = new ArrayList<>();
        String sql = "SELECT * FROM alerts ORDER BY created_at DESC";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapResultSet(rs));
        } catch (SQLException e) {
            System.err.println("[AlertDAO] Get all error: " + e.getMessage());
        }
        return list;
    }

    public List<Alert> getAlertsByLevel(String level) {
        List<Alert> list = new ArrayList<>();
        String sql = "SELECT * FROM alerts WHERE alert_level = ? ORDER BY created_at DESC";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, level);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapResultSet(rs));
        } catch (SQLException e) {
            System.err.println("[AlertDAO] Get by level error: " + e.getMessage());
        }
        return list;
    }
    public List<Alert> getAlertsByUser(int userId) {
        List<Alert> list = new ArrayList<>();
        String sql = "SELECT a.* FROM alerts a " +
                     "LEFT JOIN news_articles na ON a.article_id = na.article_id " +
                     "LEFT JOIN social_posts sp ON a.post_id = sp.post_id " +
                     "WHERE na.submitted_by = ? OR sp.submitted_by = ? ORDER BY a.created_at DESC";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapResultSet(rs));
        } catch (SQLException e) {
            System.err.println("[AlertDAO] Get by user error: " + e.getMessage());
        }
        return list;
    }
    private Alert mapResultSet(ResultSet rs) throws SQLException {
        Alert a = new Alert();
        a.setAlertId(rs.getInt("alert_id"));
        a.setContentType(rs.getString("content_type"));
        a.setArticleId(rs.getInt("article_id"));
        a.setPostId(rs.getInt("post_id"));
        a.setAlertType(rs.getString("alert_type"));
        a.setAlertLevel(rs.getString("alert_level"));
        a.setAlertMessage(rs.getString("alert_message"));
        a.setResolved(rs.getInt("is_resolved") == 1);
        a.setResolvedBy(rs.getInt("resolved_by"));
        Timestamp rt = rs.getTimestamp("resolved_at");
        if (rt != null) a.setResolvedAt(rt.toLocalDateTime());
        Timestamp ct = rs.getTimestamp("created_at");
        if (ct != null) a.setCreatedAt(ct.toLocalDateTime());
        return a;
    }


}
