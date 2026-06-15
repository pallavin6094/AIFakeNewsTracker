package com.fakenews.model;
 
import java.time.LocalDateTime;
 
/**
 * User Model - Represents a registered user of the system.
 */
public class User {
    private int userId;
    private String username;
    private String password;
    private String fullName;
    private String email;
    private String mobile;
    private LocalDateTime createdAt;
    private LocalDateTime lastLogin;
    private boolean active;
    private boolean deleted;
 
    public User() {}
 
    public User(String username, String password, String fullName, String email, String mobile) {
        this.username = username;
        this.password = password;
        this.fullName = fullName;
        this.email = email;
        this.mobile = mobile;
    }
 
    // Getters & Setters
    public int getUserId()                         { return userId; }
    public void setUserId(int userId)              { this.userId = userId; }
    public String getUsername()                    { return username; }
    public void setUsername(String username)       { this.username = username; }
    public String getPassword()                    { return password; }
    public void setPassword(String password)       { this.password = password; }
    public String getFullName()                    { return fullName; }
    public void setFullName(String fullName)       { this.fullName = fullName; }
    public String getEmail()                       { return email; }
    public void setEmail(String email)             { this.email = email; }
    public String getMobile()                      { return mobile; }
    public void setMobile(String mobile)           { this.mobile = mobile; }
    public LocalDateTime getCreatedAt()            { return createdAt; }
    public void setCreatedAt(LocalDateTime t)      { this.createdAt = t; }
    public LocalDateTime getLastLogin()            { return lastLogin; }
    public void setLastLogin(LocalDateTime t)      { this.lastLogin = t; }
    public boolean isActive()                      { return active; }
    public void setActive(boolean active)          { this.active = active; }
    public boolean isDeleted()                     { return deleted; }
    public void setDeleted(boolean deleted)        { this.deleted = deleted; }
 
    @Override
    public String toString() {
        return String.format("User[ID=%d, Username=%s, Name=%s, Email=%s, Mobile=%s]",
                userId, username, fullName, email, mobile);
    }
}
