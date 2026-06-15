package com.fakenews.dao;

import com.fakenews.connection.DBConnection;
import com.fakenews.model.User;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * UserDAO - Handles all database operations for User entity.
 */
public class UserDAO {

    /**
     * Registers a new user.
     */
    public boolean register(User user) {
        String sql = "INSERT INTO users (username, password, full_name, email, mobile) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPassword());
            ps.setString(3, user.getFullName());
            ps.setString(4, user.getEmail());
            ps.setString(5, user.getMobile());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[UserDAO] Register error: " + e.getMessage());
            return false;
        }
    }

    /**
     * Authenticates user login.
     */
    public User login(String username, String password) {
        String sql = "SELECT * FROM users WHERE username = ? AND password = ? AND is_active = 1 AND is_deleted = 0";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                User user = mapResultSet(rs);
                updateLastLogin(user.getUserId());
                return user;
            }
        } catch (SQLException e) {
            System.err.println("[UserDAO] Login error: " + e.getMessage());
        }
        return null;
    }

    /**
     * Updates user profile details.
     */
    public boolean updateProfile(User user) {
        String sql = "UPDATE users SET full_name = ?, email = ?, mobile = ? WHERE user_id = ?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, user.getFullName());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getMobile());
            ps.setInt(4, user.getUserId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[UserDAO] Update profile error: " + e.getMessage());
            return false;
        }
    }

    /**
     * Changes user password with validation.
     */
    public boolean changePassword(int userId, String oldPass, String newPass) {
        String checkSql = "SELECT user_id FROM users WHERE user_id = ? AND password = ?";
        String updateSql = "UPDATE users SET password = ? WHERE user_id = ?";
        Connection conn = DBConnection.getConnection();
        try {
            conn.setAutoCommit(false);
            try (PreparedStatement check = conn.prepareStatement(checkSql)) {
                check.setInt(1, userId);
                check.setString(2, oldPass);
                ResultSet rs = check.executeQuery();
                if (!rs.next()) { conn.rollback(); conn.setAutoCommit(true); return false; }
            }
            try (PreparedStatement update = conn.prepareStatement(updateSql)) {
                update.setString(1, newPass);
                update.setInt(2, userId);
                update.executeUpdate();
            }
            conn.commit();
            conn.setAutoCommit(true);
            return true;
        } catch (SQLException e) {
            System.err.println("[UserDAO] Change password error: " + e.getMessage());
            try { conn.rollback(); conn.setAutoCommit(true); } catch (SQLException ignored) {}
            return false;
        }
    }

    /**
     * Soft-deletes user account.
     */
    public boolean deleteAccount(int userId) {
        String sql = "UPDATE users SET is_deleted = 1, is_active = 0 WHERE user_id = ?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[UserDAO] Delete account error: " + e.getMessage());
            return false;
        }
    }

    /**
     * Checks if username is already taken.
     */
    public boolean isUsernameTaken(String username) {
        String sql = "SELECT user_id FROM users WHERE username = ?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, username);
            return ps.executeQuery().next();
        } catch (SQLException e) {
            return false;
        }
    }

    /**
     * Checks if email is already registered.
     */
    public boolean isEmailTaken(String email) {
        String sql = "SELECT user_id FROM users WHERE email = ?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, email);
            return ps.executeQuery().next();
        } catch (SQLException e) {
            return false;
        }
    }

    /**
     * Returns a user by ID.
     */
    public User getUserById(int userId) {
        String sql = "SELECT * FROM users WHERE user_id = ?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapResultSet(rs);
        } catch (SQLException e) {
            System.err.println("[UserDAO] Get user error: " + e.getMessage());
        }
        return null;
    }

    /**
     * Returns all active users (admin view).
     */
    public List<User> getAllUsers() {
        List<User> list = new ArrayList<>();
        String sql = "SELECT * FROM users WHERE is_deleted = 0 ORDER BY created_at DESC";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapResultSet(rs));
        } catch (SQLException e) {
            System.err.println("[UserDAO] Get all users error: " + e.getMessage());
        }
        return list;
    }

    /**
     * Gets total user count.
     */
    public int getTotalUserCount() {
        String sql = "SELECT COUNT(*) FROM users WHERE is_deleted = 0";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            System.err.println("[UserDAO] Count error: " + e.getMessage());
        }
        return 0;
    }

    private void updateLastLogin(int userId) {
        String sql = "UPDATE users SET last_login = ? WHERE user_id = ?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
            ps.setInt(2, userId);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("[UserDAO] Update last login error: " + e.getMessage());
        }
    }

    private User mapResultSet(ResultSet rs) throws SQLException {
        User user = new User();
        user.setUserId(rs.getInt("user_id"));
        user.setUsername(rs.getString("username"));
        user.setPassword(rs.getString("password"));
        user.setFullName(rs.getString("full_name"));
        user.setEmail(rs.getString("email"));
        user.setMobile(rs.getString("mobile"));
        user.setActive(rs.getInt("is_active") == 1);
        user.setDeleted(rs.getInt("is_deleted") == 1);
        Timestamp created = rs.getTimestamp("created_at");
        if (created != null) user.setCreatedAt(created.toLocalDateTime());
        Timestamp lastLogin = rs.getTimestamp("last_login");
        if (lastLogin != null) user.setLastLogin(lastLogin.toLocalDateTime());
        return user;
    }
}