package com.fakenews.dao;

import com.fakenews.connection.DBConnection;
import com.fakenews.model.SocialPost;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * PostDAO - Database operations for SocialPost entity.
 */
public class PostDAO {

    public int addPost(SocialPost post) {
        String sql = "INSERT INTO social_posts (platform, content, author, post_url, post_date, submitted_by) VALUES (?,?,?,?,?,?)";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, post.getPlatform());
            ps.setString(2, post.getContent());
            ps.setString(3, post.getAuthor());
            ps.setString(4, post.getPostUrl());
            ps.setDate(5, Date.valueOf(post.getPostDate()));
            ps.setInt(6, post.getSubmittedBy());
            ps.executeUpdate();
            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) return keys.getInt(1);
        } catch (SQLException e) {
            System.err.println("[PostDAO] Add error: " + e.getMessage());
        }
        return -1;
    }

    public boolean updatePost(SocialPost post) {
        String sql = "UPDATE social_posts SET platform=?, content=?, author=?, post_url=?, post_date=? WHERE post_id=? AND is_deleted=0";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, post.getPlatform());
            ps.setString(2, post.getContent());
            ps.setString(3, post.getAuthor());
            ps.setString(4, post.getPostUrl());
            ps.setDate(5, Date.valueOf(post.getPostDate()));
            ps.setInt(6, post.getPostId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[PostDAO] Update error: " + e.getMessage());
            return false;
        }
    }

    public boolean deletePost(int postId) {
        String sql = "UPDATE social_posts SET is_deleted = 1 WHERE post_id = ?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, postId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[PostDAO] Delete error: " + e.getMessage());
            return false;
        }
    }

    public SocialPost getPostById(int id) {
        String sql = "SELECT * FROM social_posts WHERE post_id = ? AND is_deleted = 0";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapResultSet(rs);
        } catch (SQLException e) {
            System.err.println("[PostDAO] Get error: " + e.getMessage());
        }
        return null;
    }

    public List<SocialPost> getAllPosts() {
        List<SocialPost> list = new ArrayList<>();
        String sql = "SELECT * FROM social_posts WHERE is_deleted = 0 ORDER BY submitted_at DESC";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapResultSet(rs));
        } catch (SQLException e) {
            System.err.println("[PostDAO] Get all error: " + e.getMessage());
        }
        return list;
    }

    public List<SocialPost> getPostsByUser(int userId) {
        List<SocialPost> list = new ArrayList<>();
        String sql = "SELECT * FROM social_posts WHERE submitted_by = ? AND is_deleted = 0 ORDER BY submitted_at DESC";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapResultSet(rs));
        } catch (SQLException e) {
            System.err.println("[PostDAO] Get by user error: " + e.getMessage());
        }
        return list;
    }

    public List<SocialPost> searchPosts(String keyword) {
        List<SocialPost> list = new ArrayList<>();
        String sql = "SELECT * FROM social_posts WHERE is_deleted = 0 AND (content LIKE ? OR author LIKE ?)";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            String k = "%" + keyword + "%";
            ps.setString(1, k); ps.setString(2, k);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapResultSet(rs));
        } catch (SQLException e) {
            System.err.println("[PostDAO] Search error: " + e.getMessage());
        }
        return list;
    }

    public int getTotalCount() {
        String sql = "SELECT COUNT(*) FROM social_posts WHERE is_deleted = 0";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) { System.err.println("[PostDAO] Count error: " + e.getMessage()); }
        return 0;
    }

    private SocialPost mapResultSet(ResultSet rs) throws SQLException {
        SocialPost p = new SocialPost();
        p.setPostId(rs.getInt("post_id"));
        p.setPlatform(rs.getString("platform"));
        p.setContent(rs.getString("content"));
        p.setAuthor(rs.getString("author"));
        p.setPostUrl(rs.getString("post_url"));
        Date pd = rs.getDate("post_date");
        if (pd != null) p.setPostDate(pd.toLocalDate());
        p.setSubmittedBy(rs.getInt("submitted_by"));
        Timestamp st = rs.getTimestamp("submitted_at");
        if (st != null) p.setSubmittedAt(st.toLocalDateTime());
        p.setDeleted(rs.getInt("is_deleted") == 1);
        return p;
    }
}
