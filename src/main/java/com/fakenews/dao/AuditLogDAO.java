package com.fakenews.dao;
import com.fakenews.connection.DBConnection;
import com.fakenews.model.AuditLog;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AuditLogDAO {
	 public boolean addLog(AuditLog log) {
	        String sql = "INSERT INTO audit_logs (user_type, user_id, username, action, description, ip_address) VALUES (?,?,?,?,?,?)";
	        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
	            ps.setString(1, log.getUserType());
	            ps.setInt(2, log.getUserId());
	            ps.setString(3, log.getUsername());
	            ps.setString(4, log.getAction());
	            ps.setString(5, log.getDescription());
	            ps.setString(6, log.getIpAddress() != null ? log.getIpAddress() : "127.0.0.1");
	            return ps.executeUpdate() > 0;
	        } catch (SQLException e) {
	            System.err.println("[AuditLogDAO] Add error: " + e.getMessage());
	            return false;
	        }
	    }

	    public List<AuditLog> getAllLogs() {
	        List<AuditLog> list = new ArrayList<>();
	        String sql = "SELECT * FROM audit_logs ORDER BY logged_at DESC";
	        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
	            ResultSet rs = ps.executeQuery();
	            while (rs.next()) list.add(mapResultSet(rs));
	        } catch (SQLException e) {
	            System.err.println("[AuditLogDAO] Get all error: " + e.getMessage());
	        }
	        return list;
	    }
	    public List<AuditLog> getLogsByUser(int userId, String userType) {
	        List<AuditLog> list = new ArrayList<>();
	        String sql = "SELECT * FROM audit_logs WHERE user_id = ? AND user_type = ? ORDER BY logged_at DESC";
	        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
	            ps.setInt(1, userId);
	            ps.setString(2, userType);
	            ResultSet rs = ps.executeQuery();
	            while (rs.next()) list.add(mapResultSet(rs));
	        } catch (SQLException e) {
	            System.err.println("[AuditLogDAO] Get by user error: " + e.getMessage());
	        }
	        return list;
	    }

	    public List<AuditLog> getLogsByAction(String action) {
	        List<AuditLog> list = new ArrayList<>();
	        String sql = "SELECT * FROM audit_logs WHERE action = ? ORDER BY logged_at DESC";
	        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
	            ps.setString(1, action);
	            ResultSet rs = ps.executeQuery();
	            while (rs.next()) list.add(mapResultSet(rs));
	        } catch (SQLException e) {
	            System.err.println("[AuditLogDAO] Get by action error: " + e.getMessage());
	        }
	        return list;
	    }
	    private AuditLog mapResultSet(ResultSet rs) throws SQLException {
	        AuditLog log = new AuditLog();
	        log.setLogId(rs.getInt("log_id"));
	        log.setUserType(rs.getString("user_type"));
	        log.setUserId(rs.getInt("user_id"));
	        log.setUsername(rs.getString("username"));
	        log.setAction(rs.getString("action"));
	        log.setDescription(rs.getString("description"));
	        log.setIpAddress(rs.getString("ip_address"));
	        Timestamp t = rs.getTimestamp("logged_at");
	        if (t != null) log.setLoggedAt(t.toLocalDateTime());
	        return log;
	    }


}
