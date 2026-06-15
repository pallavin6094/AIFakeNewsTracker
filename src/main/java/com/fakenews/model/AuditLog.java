package com.fakenews.model;

import java.time.LocalDateTime;

/**
 * AuditLog Model - Tracks all user/admin actions in the system.
 */
public class AuditLog {
    private int logId;
    private String userType;    // "Admin" or "User"
    private int userId;
    private String username;
    private String action;
    private String description;
    private String ipAddress;
    private LocalDateTime loggedAt;

    public AuditLog() {}

    public AuditLog(String userType, int userId, String username, String action, String description) {
        this.userType = userType;
        this.userId = userId;
        this.username = username;
        this.action = action;
        this.description = description;
        this.ipAddress = "127.0.0.1";
    }

    public int getLogId()                          { return logId; }
    public void setLogId(int logId)                { this.logId = logId; }
    public String getUserType()                    { return userType; }
    public void setUserType(String userType)       { this.userType = userType; }
    public int getUserId()                         { return userId; }
    public void setUserId(int userId)              { this.userId = userId; }
    public String getUsername()                    { return username; }
    public void setUsername(String username)       { this.username = username; }
    public String getAction()                      { return action; }
    public void setAction(String action)           { this.action = action; }
    public String getDescription()                 { return description; }
    public void setDescription(String desc)        { this.description = desc; }
    public String getIpAddress()                   { return ipAddress; }
    public void setIpAddress(String ipAddress)     { this.ipAddress = ipAddress; }
    public LocalDateTime getLoggedAt()             { return loggedAt; }
    public void setLoggedAt(LocalDateTime t)       { this.loggedAt = t; }

    @Override
    public String toString() {
        return String.format("Log[ID=%d, User=%s, Action=%s, At=%s]",
                logId, username, action, loggedAt);
    }
}
