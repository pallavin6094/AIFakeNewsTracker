package com.fakenews.service;

import com.fakenews.dao.AdminDAO;
import com.fakenews.dao.AuditLogDAO;
import com.fakenews.dao.UserDAO;
import com.fakenews.model.Admin;
import com.fakenews.model.AuditLog;
import com.fakenews.model.User;
import com.fakenews.util.ValidationUtil;

/**
 * AuthService - Handles authentication for Admin and User.
 */
public class AuthService {

    private final AdminDAO    adminDAO    = new AdminDAO();
    private final UserDAO     userDAO     = new UserDAO();
    private final AuditLogDAO auditLogDAO = new AuditLogDAO();

    // ── Admin Auth ────────────────────────────────────────────

    /**
     * Registers a new admin. Can only be done by an already logged-in admin.
     * Returns null on success, or an error message string on failure.
     */
    public String registerAdmin(String username, String password, String fullName,
                                String email, String mobile, Admin createdBy) {
        String err;

        err = ValidationUtil.getUsernameError(username);
        if (err != null) return err;

        err = ValidationUtil.getPasswordError(password);
        if (err != null) return err;

        err = ValidationUtil.getNameError(fullName);
        if (err != null) return err;

        err = ValidationUtil.getEmailError(email);
        if (err != null) return err;

        err = ValidationUtil.getMobileError(mobile);
        if (err != null) return err;

        if (adminDAO.isUsernameTaken(username)) return "Username '" + username + "' is already taken.";
        if (adminDAO.isEmailTaken(email))       return "Email '" + email + "' is already registered.";

        Admin newAdmin = new Admin(username, password, fullName, email, mobile);
        int id = adminDAO.registerAdmin(newAdmin);
        if (id < 0) return "Registration failed. Please try again.";

        auditLogDAO.addLog(new AuditLog("Admin", createdBy.getAdminId(), createdBy.getUsername(),
                "ADMIN_REGISTER", "Registered new admin: " + username + " (ID: " + id + ")"));
        return null; // null = success
    }

    public Admin adminLogin(String username, String password) {
        Admin admin = adminDAO.login(username, password);
        if (admin != null) {
            auditLogDAO.addLog(new AuditLog("Admin", admin.getAdminId(), admin.getUsername(), "LOGIN", "Admin logged in successfully."));
        }
        return admin;
    }

    public boolean adminChangePassword(Admin admin, String oldPass, String newPass) {
        String err = ValidationUtil.getPasswordError(newPass);
        if (err != null) { System.out.println("  [!] " + err); return false; }
        boolean ok = adminDAO.changePassword(admin.getAdminId(), oldPass, newPass);
        if (ok) {
            auditLogDAO.addLog(new AuditLog("Admin", admin.getAdminId(), admin.getUsername(), "CHANGE_PASSWORD", "Admin changed password."));
        }
        return ok;
    }

    public void adminLogout(Admin admin) {
        auditLogDAO.addLog(new AuditLog("Admin", admin.getAdminId(), admin.getUsername(), "LOGOUT", "Admin logged out."));
    }

    // ── User Auth ─────────────────────────────────────────────

    /**
     * Registers a new user with full validation.
     * Returns error message string or null on success.
     */
    public String registerUser(String username, String password, String fullName, String email, String mobile) {
        String err;

        err = ValidationUtil.getUsernameError(username);
        if (err != null) return err;

        err = ValidationUtil.getPasswordError(password);
        if (err != null) return err;

        err = ValidationUtil.getNameError(fullName);
        if (err != null) return err;

        err = ValidationUtil.getEmailError(email);
        if (err != null) return err;

        err = ValidationUtil.getMobileError(mobile);
        if (err != null) return err;

        if (userDAO.isUsernameTaken(username)) return "Username '" + username + "' is already taken.";
        if (userDAO.isEmailTaken(email))       return "Email '" + email + "' is already registered.";

        User user = new User(username, password, fullName, email, mobile);
        boolean ok = userDAO.register(user);
        if (!ok) return "Registration failed. Please try again.";

        // Log registration
        User created = userDAO.login(username, password);
        if (created != null) {
            auditLogDAO.addLog(new AuditLog("User", created.getUserId(), created.getUsername(), "REGISTER", "New user registered."));
        }
        return null;
    }

    public User userLogin(String username, String password) {
        User user = userDAO.login(username, password);
        if (user != null) {
            auditLogDAO.addLog(new AuditLog("User", user.getUserId(), user.getUsername(), "LOGIN", "User logged in successfully."));
        }
        return user;
    }

    public boolean userChangePassword(User user, String oldPass, String newPass) {
        String err = ValidationUtil.getPasswordError(newPass);
        if (err != null) { System.out.println("  [!] " + err); return false; }
        boolean ok = userDAO.changePassword(user.getUserId(), oldPass, newPass);
        if (ok) {
            auditLogDAO.addLog(new AuditLog("User", user.getUserId(), user.getUsername(), "CHANGE_PASSWORD", "User changed password."));
        }
        return ok;
    }

    public boolean updateProfile(User user) {
        String err;
        err = ValidationUtil.getNameError(user.getFullName());
        if (err != null) { System.out.println("  [!] " + err); return false; }
        err = ValidationUtil.getEmailError(user.getEmail());
        if (err != null) { System.out.println("  [!] " + err); return false; }
        err = ValidationUtil.getMobileError(user.getMobile());
        if (err != null) { System.out.println("  [!] " + err); return false; }
        boolean ok = userDAO.updateProfile(user);
        if (ok) {
            auditLogDAO.addLog(new AuditLog("User", user.getUserId(), user.getUsername(), "UPDATE_PROFILE", "User updated profile."));
        }
        return ok;
    }

    public boolean deleteAccount(User user) {
        boolean ok = userDAO.deleteAccount(user.getUserId());
        if (ok) {
            auditLogDAO.addLog(new AuditLog("User", user.getUserId(), user.getUsername(), "DELETE_ACCOUNT", "User deleted their account."));
        }
        return ok;
    }

    public void userLogout(User user) {
        auditLogDAO.addLog(new AuditLog("User", user.getUserId(), user.getUsername(), "LOGOUT", "User logged out."));
    }
}