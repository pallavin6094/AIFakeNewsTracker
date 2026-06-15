package com.fakenews.service;

import com.fakenews.connection.DBConnection;
import com.fakenews.dao.*;
import com.fakenews.model.Admin;
import com.fakenews.model.AuditLog;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * ReportService - Generates all admin-level reports using JDBC queries.
 */
public class ReportService {

    private final ArticleDAO  articleDAO  = new ArticleDAO();
    private final PostDAO     postDAO     = new PostDAO();
    private final UserDAO     userDAO     = new UserDAO();
    private final AnalysisDAO analysisDAO = new AnalysisDAO();
    private final AuditLogDAO auditLogDAO = new AuditLogDAO();

    // ── Summary Report ────────────────────────────────────────

    public void printSummaryReport(Admin admin) {
        System.out.println("\n  ╔══════════════════════════════════════════════════╗");
        System.out.println("  ║           SYSTEM SUMMARY REPORT                 ║");
        System.out.println("  ╠══════════════════════════════════════════════════╣");
        System.out.printf("  ║  Total Users              : %-5d               ║%n", userDAO.getTotalUserCount());
        System.out.printf("  ║  Total Articles           : %-5d               ║%n", articleDAO.getTotalCount());
        System.out.printf("  ║  Total Social Posts       : %-5d               ║%n", postDAO.getTotalCount());
        System.out.printf("  ║  Fake News Detected       : %-5d               ║%n", analysisDAO.getFakeNewsCount());
        System.out.printf("  ║  High Risk Content        : %-5d               ║%n", analysisDAO.getHighRiskCount());
        System.out.println("  ╚══════════════════════════════════════════════════╝");
        auditLogDAO.addLog(new AuditLog("Admin", admin.getAdminId(), admin.getUsername(),
                "REPORT", "Generated Summary Report."));
    }

    // ── Source Reliability Report ─────────────────────────────

    public void printSourceReliabilityReport(Admin admin) {
        String sql = "SELECT source_name, reliability_level, reliability_score, total_articles, misinfo_count " +
                     "FROM news_sources ORDER BY reliability_score DESC";
        System.out.println("\n  SOURCE RELIABILITY REPORT");
        System.out.println("  ─────────────────────────────────────────────────────────────────────");
        System.out.printf("  %-25s %-18s %8s %10s %10s%n",
                "Source Name", "Level", "Score", "Articles", "Misinfo");
        System.out.println("  ─────────────────────────────────────────────────────────────────────");
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                System.out.printf("  %-25s %-18s %8.1f %10d %10d%n",
                        rs.getString("source_name"),
                        rs.getString("reliability_level"),
                        rs.getDouble("reliability_score"),
                        rs.getInt("total_articles"),
                        rs.getInt("misinfo_count"));
            }
        } catch (SQLException e) {
            System.err.println("  [ReportService] Source report error: " + e.getMessage());
        }
        auditLogDAO.addLog(new AuditLog("Admin", admin.getAdminId(), admin.getUsername(),
                "REPORT", "Generated Source Reliability Report."));
    }

    // ── Viral Spread Report ───────────────────────────────────

    public void printViralSpreadReport(Admin admin) {
        String sql = "SELECT st.platform, COUNT(*) AS events, SUM(st.reach_count) AS total_reach, " +
                     "na.title AS article_title " +
                     "FROM spread_tracking st " +
                     "LEFT JOIN news_articles na ON st.article_id = na.article_id " +
                     "GROUP BY st.platform, na.title ORDER BY total_reach DESC LIMIT 20";
        System.out.println("\n  VIRAL SPREAD REPORT (Top 20)");
        System.out.println("  ─────────────────────────────────────────────────────────────────────");
        System.out.printf("  %-15s %8s %15s  %-30s%n", "Platform", "Events", "Total Reach", "Article/Post");
        System.out.println("  ─────────────────────────────────────────────────────────────────────");
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            boolean found = false;
            while (rs.next()) {
                found = true;
                String title = rs.getString("article_title");
                System.out.printf("  %-15s %8d %15s  %-30s%n",
                        rs.getString("platform"),
                        rs.getInt("events"),
                        String.format("%,d", rs.getInt("total_reach")),
                        title != null ? title.substring(0, Math.min(title.length(), 30)) : "Social Post");
            }
            if (!found) System.out.println("  No spread events recorded yet.");
        } catch (SQLException e) {
            System.err.println("  [ReportService] Spread report error: " + e.getMessage());
        }
        auditLogDAO.addLog(new AuditLog("Admin", admin.getAdminId(), admin.getUsername(),
                "REPORT", "Generated Viral Spread Report."));
    }

    // ── Platform-wise Misinformation Report ───────────────────

    public void printPlatformMisinfoReport(Admin admin) {
        String sql = "SELECT sp.platform, COUNT(*) AS total_posts, " +
                     "SUM(CASE WHEN ca.classification IN ('Fake News','Misinformation Risk') THEN 1 ELSE 0 END) AS misinfo_count " +
                     "FROM social_posts sp " +
                     "LEFT JOIN credibility_analysis ca ON ca.post_id = sp.post_id AND ca.content_type='Post' " +
                     "WHERE sp.is_deleted = 0 " +
                     "GROUP BY sp.platform ORDER BY misinfo_count DESC";
        System.out.println("\n  PLATFORM-WISE MISINFORMATION REPORT");
        System.out.println("  ─────────────────────────────────────────────────");
        System.out.printf("  %-18s %12s %15s%n", "Platform", "Total Posts", "Misinfo Count");
        System.out.println("  ─────────────────────────────────────────────────");
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            boolean found = false;
            while (rs.next()) {
                found = true;
                System.out.printf("  %-18s %12d %15d%n",
                        rs.getString("platform"),
                        rs.getInt("total_posts"),
                        rs.getInt("misinfo_count"));
            }
            if (!found) System.out.println("  No social posts found.");
        } catch (SQLException e) {
            System.err.println("  [ReportService] Platform report error: " + e.getMessage());
        }
        auditLogDAO.addLog(new AuditLog("Admin", admin.getAdminId(), admin.getUsername(),
                "REPORT", "Generated Platform Misinformation Report."));
    }

    // ── Monthly Analysis Report ───────────────────────────────

    public void printMonthlyAnalysisReport(Admin admin) {
        String sql = "SELECT DATE_FORMAT(analyzed_at, '%Y-%m') AS month, " +
                     "COUNT(*) AS total_analyzed, " +
                     "SUM(CASE WHEN classification='Fake News' THEN 1 ELSE 0 END) AS fake_news, " +
                     "SUM(CASE WHEN classification='Highly Credible' OR classification='Credible' THEN 1 ELSE 0 END) AS credible, " +
                     "AVG(final_credibility_score) AS avg_score " +
                     "FROM credibility_analysis " +
                     "GROUP BY month ORDER BY month DESC LIMIT 12";
        System.out.println("\n  MONTHLY ANALYSIS REPORT (Last 12 Months)");
        System.out.println("  ──────────────────────────────────────────────────────────");
        System.out.printf("  %-10s %10s %10s %10s %12s%n",
                "Month", "Analyzed", "Fake News", "Credible", "Avg Score");
        System.out.println("  ──────────────────────────────────────────────────────────");
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            boolean found = false;
            while (rs.next()) {
                found = true;
                System.out.printf("  %-10s %10d %10d %10d %12.1f%n",
                        rs.getString("month"),
                        rs.getInt("total_analyzed"),
                        rs.getInt("fake_news"),
                        rs.getInt("credible"),
                        rs.getDouble("avg_score"));
            }
            if (!found) System.out.println("  No analysis data available.");
        } catch (SQLException e) {
            System.err.println("  [ReportService] Monthly report error: " + e.getMessage());
        }
        auditLogDAO.addLog(new AuditLog("Admin", admin.getAdminId(), admin.getUsername(),
                "REPORT", "Generated Monthly Analysis Report."));
    }

    // ── High Risk Articles Report ─────────────────────────────

    public void printHighRiskArticlesReport(Admin admin) {
        String sql = "SELECT na.article_id, na.title, na.source_name, " +
                     "ca.final_credibility_score, ca.classification " +
                     "FROM news_articles na " +
                     "JOIN credibility_analysis ca ON ca.article_id = na.article_id AND ca.content_type='Article' " +
                     "WHERE ca.classification IN ('Fake News','Misinformation Risk') AND na.is_deleted=0 " +
                     "ORDER BY ca.final_credibility_score ASC LIMIT 20";
        System.out.println("\n  HIGH RISK ARTICLES REPORT");
        System.out.println("  ─────────────────────────────────────────────────────────────────────");
        System.out.printf("  %5s  %-35s %-15s %8s  %-20s%n",
                "ID", "Title", "Source", "Score", "Classification");
        System.out.println("  ─────────────────────────────────────────────────────────────────────");
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            boolean found = false;
            while (rs.next()) {
                found = true;
                String title = rs.getString("title");
                System.out.printf("  %5d  %-35s %-15s %8.1f  %-20s%n",
                        rs.getInt("article_id"),
                        title.substring(0, Math.min(title.length(), 35)),
                        rs.getString("source_name") != null ? rs.getString("source_name").substring(0, Math.min(rs.getString("source_name").length(), 15)) : "N/A",
                        rs.getDouble("final_credibility_score"),
                        rs.getString("classification"));
            }
            if (!found) System.out.println("  No high-risk articles found.");
        } catch (SQLException e) {
            System.err.println("  [ReportService] High risk report error: " + e.getMessage());
        }
        auditLogDAO.addLog(new AuditLog("Admin", admin.getAdminId(), admin.getUsername(),
                "REPORT", "Generated High Risk Articles Report."));
    }

    // ── Audit Log Report ──────────────────────────────────────

    public void printAuditLogReport(Admin admin) {
        String sql = "SELECT log_id, user_type, username, action, description, logged_at " +
                     "FROM audit_logs ORDER BY logged_at DESC LIMIT 50";
        System.out.println("\n  AUDIT LOG REPORT (Last 50 entries)");
        System.out.println("  ──────────────────────────────────────────────────────────────────");
        System.out.printf("  %5s  %-8s %-15s %-20s %-25s  %s%n",
                "ID", "Type", "Username", "Action", "Description", "Logged At");
        System.out.println("  ──────────────────────────────────────────────────────────────────");
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String desc = rs.getString("description");
                System.out.printf("  %5d  %-8s %-15s %-20s %-25s  %s%n",
                        rs.getInt("log_id"),
                        rs.getString("user_type"),
                        rs.getString("username"),
                        rs.getString("action"),
                        desc != null ? desc.substring(0, Math.min(desc.length(), 25)) : "",
                        rs.getTimestamp("logged_at"));
            }
        } catch (SQLException e) {
            System.err.println("  [ReportService] Audit log report error: " + e.getMessage());
        }
    }
}