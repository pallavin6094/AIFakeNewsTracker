package com.fakenews.dao;

import com.fakenews.connection.DBConnection;
import com.fakenews.model.NewsArticle;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * ArticleDAO - Database operations for NewsArticle entity.
 */
public class ArticleDAO {

    /**
     * Inserts a new article. Returns generated ID or -1 on failure.
     */
    public int addArticle(NewsArticle article) {
        String sql = "INSERT INTO news_articles (title, content, author, source_id, source_name, source_url, category, publication_date, submitted_by) VALUES (?,?,?,?,?,?,?,?,?)";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, article.getTitle());
            ps.setString(2, article.getContent());
            ps.setString(3, article.getAuthor());
            if (article.getSourceId() > 0)
                ps.setInt(4, article.getSourceId());
            else
                ps.setNull(4, Types.INTEGER);
            ps.setString(5, article.getSourceName());
            ps.setString(6, article.getSourceUrl());
            ps.setString(7, article.getCategory());
            ps.setDate(8, Date.valueOf(article.getPublicationDate()));
            ps.setInt(9, article.getSubmittedBy());
            ps.executeUpdate();
            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) return keys.getInt(1);
        } catch (SQLException e) {
            System.err.println("[ArticleDAO] Add error: " + e.getMessage());
        }
        return -1;
    }

    /**
     * Updates an existing article.
     */
    public boolean updateArticle(NewsArticle article) {
        String sql = "UPDATE news_articles SET title=?, content=?, author=?, source_name=?, source_url=?, category=?, publication_date=? WHERE article_id=? AND is_deleted=0";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, article.getTitle());
            ps.setString(2, article.getContent());
            ps.setString(3, article.getAuthor());
            ps.setString(4, article.getSourceName());
            ps.setString(5, article.getSourceUrl());
            ps.setString(6, article.getCategory());
            ps.setDate(7, Date.valueOf(article.getPublicationDate()));
            ps.setInt(8, article.getArticleId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[ArticleDAO] Update error: " + e.getMessage());
            return false;
        }
    }

    /**
     * Soft-deletes an article by ID.
     */
    public boolean deleteArticle(int articleId) {
        String sql = "UPDATE news_articles SET is_deleted = 1 WHERE article_id = ?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, articleId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[ArticleDAO] Delete error: " + e.getMessage());
            return false;
        }
    }

    /**
     * Returns article by ID.
     */
    public NewsArticle getArticleById(int id) {
        String sql = "SELECT * FROM news_articles WHERE article_id = ? AND is_deleted = 0";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapResultSet(rs);
        } catch (SQLException e) {
            System.err.println("[ArticleDAO] Get error: " + e.getMessage());
        }
        return null;
    }

    /**
     * Returns all active articles.
     */
    public List<NewsArticle> getAllArticles() {
        List<NewsArticle> list = new ArrayList<>();
        String sql = "SELECT * FROM news_articles WHERE is_deleted = 0 ORDER BY submitted_at DESC";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapResultSet(rs));
        } catch (SQLException e) {
            System.err.println("[ArticleDAO] Get all error: " + e.getMessage());
        }
        return list;
    }

    /**
     * Returns articles submitted by a specific user.
     */
    public List<NewsArticle> getArticlesByUser(int userId) {
        List<NewsArticle> list = new ArrayList<>();
        String sql = "SELECT * FROM news_articles WHERE submitted_by = ? AND is_deleted = 0 ORDER BY submitted_at DESC";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapResultSet(rs));
        } catch (SQLException e) {
            System.err.println("[ArticleDAO] Get by user error: " + e.getMessage());
        }
        return list;
    }

    /**
     * Searches articles by keyword in title or content.
     */
    public List<NewsArticle> searchArticles(String keyword) {
        List<NewsArticle> list = new ArrayList<>();
        String sql = "SELECT * FROM news_articles WHERE is_deleted = 0 AND (title LIKE ? OR content LIKE ? OR author LIKE ?)";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            String k = "%" + keyword + "%";
            ps.setString(1, k); ps.setString(2, k); ps.setString(3, k);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapResultSet(rs));
        } catch (SQLException e) {
            System.err.println("[ArticleDAO] Search error: " + e.getMessage());
        }
        return list;
    }

    /**
     * Total article count.
     */
    public int getTotalCount() {
        String sql = "SELECT COUNT(*) FROM news_articles WHERE is_deleted = 0";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            System.err.println("[ArticleDAO] Count error: " + e.getMessage());
        }
        return 0;
    }

    private NewsArticle mapResultSet(ResultSet rs) throws SQLException {
        NewsArticle a = new NewsArticle();
        a.setArticleId(rs.getInt("article_id"));
        a.setTitle(rs.getString("title"));
        a.setContent(rs.getString("content"));
        a.setAuthor(rs.getString("author"));
        a.setSourceId(rs.getInt("source_id"));
        a.setSourceName(rs.getString("source_name"));
        a.setSourceUrl(rs.getString("source_url"));
        a.setCategory(rs.getString("category"));
        Date pd = rs.getDate("publication_date");
        if (pd != null) a.setPublicationDate(pd.toLocalDate());
        a.setSubmittedBy(rs.getInt("submitted_by"));
        Timestamp st = rs.getTimestamp("submitted_at");
        if (st != null) a.setSubmittedAt(st.toLocalDateTime());
        a.setDeleted(rs.getInt("is_deleted") == 1);
        return a;
    }
}
