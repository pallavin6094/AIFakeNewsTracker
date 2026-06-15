package com.fakenews.dao;

import com.fakenews.connection.DBConnection;
import com.fakenews.model.Admin;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * AdminDAO - Handles all database operations for Admin entity.
 */
public class AdminDAO {

    /**
     * Registers a new admin. Only callable by an existing logged-in admin.
     * Returns generated admin_id or -1 on failure.
     */
    public int registerAdmin(Admin admin) {
        String sql = "INSERT INTO admins (username, password, full_name, email, mobile) VALUES (?,?,?,?,?)";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, admin.getUsername());
            ps.setString(2, admin.getPassword());
            ps.setString(3, admin.getFullName());
            ps.setString(4, admin.getEmail());
            ps.setString(5, admin.getMobile());
            ps.executeUpdate();
            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) return keys.getInt(1);
        } catch (SQLException e) {
            System.err.println("[AdminDAO] Register error: " + e.getMessage());
        }
        return -1;
    }

    /**
     * Checks if admin username is already taken.
     */
    public boolean isUsernameTaken(String username) {
        String sql = "SELECT admin_id FROM admins WHERE username = ?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, username);
            return ps.executeQuery().next();
        } catch (SQLException e) { return false; }
    }

    /**
     * Checks if admin email is already registered.
     */
    public boolean isEmailTaken(String email) {
        String sql = "SELECT admin_id FROM admins WHERE email = ?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, email);
            return ps.executeQuery().next();
        } catch (SQLException e) { return false; }
    }

    /**
     * Returns all admins.
     */
    public List<Admin> getAllAdmins() {
        List<Admin> list = new ArrayList<>();
        String sql = "SELECT * FROM admins ORDER BY created_at DESC";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapResultSet(rs));
        } catch (SQLException e) {
            System.err.println("[AdminDAO] Get all error: " + e.getMessage());
        }
        return list;
    }

    /**
     * Deactivates an admin account (cannot deactivate self).
     */
    public boolean deactivateAdmin(int adminId) {
        String sql = "UPDATE admins SET is_active = 0 WHERE admin_id = ?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, adminId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[AdminDAO] Deactivate error: " + e.getMessage());
            return false;
        }
    }

    /**
     * Authenticates admin by username and password.
     */
    public Admin login(String username, String password) {
        String sql = "SELECT * FROM admins WHERE username = ? AND password = ? AND is_active = 1";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Admin admin = mapResultSet(rs);
                updateLastLogin(admin.getAdminId());
                return admin;
            }
        } catch (SQLException e) {
            System.err.println("[AdminDAO] Login error: " + e.getMessage());
        }
        return null;
    }

    /**
     * Changes admin password.
     */
    public boolean changePassword(int adminId, String oldPassword, String newPassword) {
        String checkSql = "SELECT admin_id FROM admins WHERE admin_id = ? AND password = ?";
        String updateSql = "UPDATE admins SET password = ? WHERE admin_id = ?";
        try {
            Connection conn = DBConnection.getConnection();
            conn.setAutoCommit(false);
            try (PreparedStatement check = conn.prepareStatement(checkSql)) {
                check.setInt(1, adminId);
                check.setString(2, oldPassword);
                ResultSet rs = check.executeQuery();
                if (!rs.next()) {
                    conn.rollback();
                    return false;
                }
            }
            try (PreparedStatement update = conn.prepareStatement(updateSql)) {
                update.setString(1, newPassword);
                update.setInt(2, adminId);
                update.executeUpdate();
            }
            conn.commit();
            conn.setAutoCommit(true);
            return true;
        } catch (SQLException e) {
            System.err.println("[AdminDAO] Change password error: " + e.getMessage());
        }
        return false;
    }

    /**
     * Updates last login timestamp for admin.
     */
    private void updateLastLogin(int adminId) {
        String sql = "UPDATE admins SET last_login = ? WHERE admin_id = ?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
            ps.setInt(2, adminId);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("[AdminDAO] Update last login error: " + e.getMessage());
        }
    }

    /**
     * Maps ResultSet row to Admin object.
     */
    private Admin mapResultSet(ResultSet rs) throws SQLException {
        Admin admin = new Admin();
        admin.setAdminId(rs.getInt("admin_id"));
        admin.setUsername(rs.getString("username"));
        admin.setPassword(rs.getString("password"));
        admin.setFullName(rs.getString("full_name"));
        admin.setEmail(rs.getString("email"));
        admin.setMobile(rs.getString("mobile"));
        admin.setActive(rs.getInt("is_active") == 1);
        Timestamp created = rs.getTimestamp("created_at");
        if (created != null) admin.setCreatedAt(created.toLocalDateTime());
        Timestamp lastLogin = rs.getTimestamp("last_login");
        if (lastLogin != null) admin.setLastLogin(lastLogin.toLocalDateTime());
        return admin;
    }
}