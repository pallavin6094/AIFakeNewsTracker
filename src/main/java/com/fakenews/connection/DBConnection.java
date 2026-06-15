package com.fakenews.connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
 
/**
 * DBConnection - Singleton JDBC Connection Manager
 * Manages MySQL database connection for AI Fake News Tracker
 */
public class DBConnection {
 
    // ── Database Configuration ──────────────────────────────
    private static final String URL      = "jdbc:mysql://localhost:3306/ai_fakenews_tracker?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "root"; // Change to your MySQL password
 
    private static Connection connection = null;
 
    // Private constructor - Singleton
    private DBConnection() {}
 
    /**
     * Returns a singleton Connection instance.
     * Re-creates connection if closed.
     */
    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
                System.out.println("[DB] Connected to MySQL successfully.");
            }
        } catch (ClassNotFoundException e) {
            System.err.println("[DB ERROR] MySQL Driver not found: " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("[DB ERROR] Connection failed: " + e.getMessage());
        }
        return connection;
    }
 
    /**
     * Closes the database connection safely.
     */
    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("[DB] Connection closed.");
            }
        } catch (SQLException e) {
            System.err.println("[DB ERROR] Failed to close connection: " + e.getMessage());
        }
    }
}