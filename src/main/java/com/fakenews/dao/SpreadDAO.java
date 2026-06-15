package com.fakenews.dao;

import com.fakenews.connection.DBConnection;
import com.fakenews.model.SpreadTracking;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SpreadDAO {
	public int addSpreadEvent(SpreadTracking st) {
        String sql = "INSERT INTO spread_tracking (content_type, article_id, post_id, original_source, reposted_by, platform, spread_date, reach_count, spread_notes, recorded_by) VALUES (?,?,?,?,?,?,?,?,?,?)";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, st.getContentType());
            if (st.getArticleId() > 0) ps.setInt(2, st.getArticleId()); else ps.setNull(2, Types.INTEGER);
            if (st.getPostId() > 0) ps.setInt(3, st.getPostId()); else ps.setNull(3, Types.INTEGER);
            ps.setString(4, st.getOriginalSource());
            ps.setString(5, st.getRepostedBy());
            ps.setString(6, st.getPlatform());
            ps.setDate(7, Date.valueOf(st.getSpreadDate()));
            ps.setInt(8, st.getReachCount());
            ps.setString(9, st.getSpreadNotes());
            ps.setInt(10, st.getRecordedBy());
            ps.executeUpdate();
            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) return keys.getInt(1);
        } catch (SQLException e) {
            System.err.println("[SpreadDAO] Add error: " + e.getMessage());
        }
        return -1;
    }
	public List<SpreadTracking> getSpreadByArticle(int articleId) {
        List<SpreadTracking> list = new ArrayList<>();
        String sql = "SELECT * FROM spread_tracking WHERE article_id = ? ORDER BY spread_date ASC";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, articleId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapResultSet(rs));
        } catch (SQLException e) {
            System.err.println("[SpreadDAO] Get by article error: " + e.getMessage());
        }
        return list;
    }
	public List<SpreadTracking> getSpreadByPost(int postId) {
        List<SpreadTracking> list = new ArrayList<>();
        String sql = "SELECT * FROM spread_tracking WHERE post_id = ? ORDER BY spread_date ASC";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, postId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapResultSet(rs));
        } catch (SQLException e) {
            System.err.println("[SpreadDAO] Get by post error: " + e.getMessage());
        }
        return list;
    }
	public List<SpreadTracking> getAllSpreadEvents() {
        List<SpreadTracking> list = new ArrayList<>();
        String sql = "SELECT * FROM spread_tracking ORDER BY recorded_at DESC";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapResultSet(rs));
        } catch (SQLException e) {
            System.err.println("[SpreadDAO] Get all error: " + e.getMessage());
        }
        return list;
    }

    public int getTotalReachByArticle(int articleId) {
        String sql = "SELECT COALESCE(SUM(reach_count),0) FROM spread_tracking WHERE article_id = ?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, articleId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            System.err.println("[SpreadDAO] Total reach error: " + e.getMessage());
        }
        return 0;
    }
    private SpreadTracking mapResultSet(ResultSet rs) throws SQLException {
        SpreadTracking s = new SpreadTracking();
        s.setSpreadId(rs.getInt("spread_id"));
        s.setContentType(rs.getString("content_type"));
        s.setArticleId(rs.getInt("article_id"));
        s.setPostId(rs.getInt("post_id"));
        s.setOriginalSource(rs.getString("original_source"));
        s.setRepostedBy(rs.getString("reposted_by"));
        s.setPlatform(rs.getString("platform"));
        Date d = rs.getDate("spread_date");
        if (d != null) s.setSpreadDate(d.toLocalDate());
        s.setReachCount(rs.getInt("reach_count"));
        s.setSpreadNotes(rs.getString("spread_notes"));
        s.setRecordedBy(rs.getInt("recorded_by"));
        Timestamp t = rs.getTimestamp("recorded_at");
        if (t != null) s.setRecordedAt(t.toLocalDateTime());
        return s;
    }

}
