package com.fakenews.model;


import java.time.LocalDateTime;
 
/**
 * Admin Model - Represents an admin user in the system.
 */
public class Admin {
    private int adminId;
    private String username;
    private String password;
    private String fullName;
    private String email;
    private String mobile;
    private LocalDateTime createdAt;
    private LocalDateTime lastLogin;
    private boolean active;
 
    public Admin() {}
 
    public Admin(String username, String password, String fullName, String email, String mobile) {
        this.username = username;
        this.password = password;
        this.fullName = fullName;
        this.email = email;
        this.mobile = mobile;
    }
 
    // Getters & Setters
    public int getAdminId()                        { return adminId; }
    public void setAdminId(int adminId)            { this.adminId = adminId; }
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
 
    @Override
    public String toString() {
        return String.format("Admin[ID=%d, Username=%s, Name=%s, Email=%s]",
                adminId, username, fullName, email);
    }
}