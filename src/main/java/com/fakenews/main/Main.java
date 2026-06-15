package com.fakenews.main;

import com.fakenews.connection.DBConnection;
import com.fakenews.dao.*;
import com.fakenews.model.*;
import com.fakenews.service.*;
import com.fakenews.util.InputUtil;
import com.fakenews.util.ValidationUtil;

import java.time.LocalDate;
import java.util.List;

/**
 * Main.java - Entry point for AI Fake News & Misinformation Source Tracker
 * Menu-driven console application using Java JDBC + MySQL.
 *
 * Default Admin → username: admin | password: Admin@123
 */
public class Main {

    // ── Services & DAOs ───────────────────────────────────────
    private static final AuthService     authService     = new AuthService();
    private static final AnalysisService analysisService = new AnalysisService();
    private static final FactCheckService factCheckService = new FactCheckService();
    private static final SpreadService   spreadService   = new SpreadService();
    private static final AlertService    alertService    = new AlertService();
    private static final ReportService   reportService   = new ReportService();

    private static final ArticleDAO      articleDAO      = new ArticleDAO();
    private static final PostDAO         postDAO         = new PostDAO();
    private static final SourceDAO       sourceDAO       = new SourceDAO();
    private static final UserDAO         userDAO         = new UserDAO();
    private static final AuditLogDAO     auditLogDAO     = new AuditLogDAO();

    // ── Session ───────────────────────────────────────────────
    private static Admin currentAdmin = null;
    private static User  currentUser  = null;

    // ─────────────────────────────────────────────────────────
    // MAIN
    // ─────────────────────────────────────────────────────────
    public static void main(String[] args) {
        printBanner();
        boolean running = true;
        while (running) {
            printMainMenu();

            int choice = InputUtil.readIntInRange(
                    "  Enter choice: ", 1, 5);

            switch (choice) {
                case 1:
                    adminLoginFlow();
                    break;


                case 2:
                    userLoginFlow();
                    break;

                case 3:
                    userRegisterFlow();
                    break;

                case 4:
                    running = false;
                    break;
            }
        }
        System.out.println("\n  Thank you for using AI Fake News Tracker. Goodbye!\n");
    }

    // ─────────────────────────────────────────────────────────
    // BANNER & MENUS
    // ─────────────────────────────────────────────────────────
    private static void printBanner() {
        System.out.println("\n");
        System.out.println("  ╔══════════════════════════════════════════════════════════════╗");
        System.out.println("  ║     AI FAKE NEWS & MISINFORMATION SOURCE TRACKER  v1.0      ║");
        System.out.println("  ║          Final Year BCA / B.Tech Java JDBC Project           ║");
        System.out.println("  ╚══════════════════════════════════════════════════════════════╝");
        System.out.println("  Detecting misinformation. Protecting the truth.\n");
    }

    private static void printMainMenu() {
        InputUtil.printHeader("MAIN MENU");
        System.out.println("  1. Admin Login");
        System.out.println("  2. User Login");
        System.out.println("  3. User Registration");
        System.out.println("  4. Exit");
        InputUtil.printDivider();
    }

    // ─────────────────────────────────────────────────────────
    // ADMIN LOGIN FLOW
    // ─────────────────────────────────────────────────────────
    private static void adminLoginFlow() {
        InputUtil.printHeader("ADMIN LOGIN");
        String username = InputUtil.readRequiredString("  Username : ");
        String password = InputUtil.readPassword("  Password : ");
        currentAdmin = authService.adminLogin(username, password);
        if (currentAdmin == null) {
            System.out.println("\n  [!] Invalid credentials. Access denied.");
            InputUtil.pause();
            return;
        }
        System.out.println("\n  Welcome, " + currentAdmin.getFullName() + "!");
        adminDashboard();
        currentAdmin = null;
    }

    // ─────────────────────────────────────────────────────────
    // ADMIN DASHBOARD
    // ─────────────────────────────────────────────────────────
    private static void adminDashboard() {
        boolean active = true;
        while (active) {
            InputUtil.printHeader("ADMIN DASHBOARD — " + currentAdmin.getUsername());
            System.out.println("  1.  Manage Admins");
            System.out.println("  2.  Manage Users");
            System.out.println("  3.  Manage News Articles");
            System.out.println("  4.  Manage Social Posts");
            System.out.println("  5.  Manage News Sources");
            System.out.println("  6.  View Credibility Analysis");
            System.out.println("  7.  View Fact Checks");
            System.out.println("  8.  Manage Alerts");
            System.out.println("  9.  Spread Tracking");
            System.out.println("  10. Reports");
            System.out.println("  11. Change Password");
            System.out.println("  12. Logout");
            InputUtil.printDivider();
            int choice = InputUtil.readIntInRange("  Enter choice: ", 1, 12);
            switch (choice) {
                case 1:  adminManageAdmins();         break;
                case 2:  adminManageUsers();          break;
                case 3:  adminManageArticles();       break;
                case 4:  adminManagePosts();          break;
                case 5:  adminManageSources();        break;
                case 6:  adminViewAnalysis();         break;
                case 7:  adminViewFactChecks();       break;
                case 8:  adminManageAlerts();         break;
                case 9:  adminViewSpread();           break;
                case 10: adminReports();              break;
                case 11: adminChangePassword();       break;
                case 12: authService.adminLogout(currentAdmin); active = false; break;
            }
        }
    }

    // ── Admin: Manage Admins ──────────────────────────────────
    private static void adminManageAdmins() {
        boolean back = false;
        while (!back) {
            InputUtil.printHeader("MANAGE ADMINS");
            System.out.println("  1. Register New Admin");
            System.out.println("  2. View All Admins");
            System.out.println("  3. Deactivate Admin");
            System.out.println("  4. Back");
            InputUtil.printDivider();
            int choice = InputUtil.readIntInRange("  Enter choice: ", 1, 4);
            switch (choice) {
                case 1: registerNewAdmin();  break;
                case 2: viewAllAdmins();     break;
                case 3: deactivateAdmin();   break;
                case 4: back = true;         break;
            }
        }
    }

    private static void registerNewAdmin() {
        InputUtil.printHeader("REGISTER NEW ADMIN");
        System.out.println("  [Note] You are registering a new admin account.");
        System.out.println("         New admin will have full system access.\n");

        String username = InputUtil.readRequiredString("  Username  (5–20 chars)              : ");
        String fullName = InputUtil.readRequiredString("  Full Name (alphabets only)           : ");
        String email    = InputUtil.readRequiredString("  Email                               : ");
        String mobile   = InputUtil.readRequiredString("  Mobile    (10 digits)               : ");
        String password = InputUtil.readPassword(      "  Password  (min 8,A-Z,a-z,0-9,@#$!) : ");
        String confirm  = InputUtil.readPassword(      "  Confirm Password                    : ");

        if (!password.equals(confirm)) {
            System.out.println("\n  [!] Passwords do not match. Registration cancelled.");
            InputUtil.pause();
            return;
        }

        if (!InputUtil.readConfirm("\n  Confirm registering admin '" + username + "'?")) {
            System.out.println("  Registration cancelled.");
            InputUtil.pause();
            return;
        }

        String error = authService.registerAdmin(username, password, fullName, email, mobile, currentAdmin);
        if (error != null) {
            System.out.println("\n  [!] Registration failed: " + error);
        } else {
            System.out.println("\n  ✔ Admin '" + username + "' registered successfully!");
            System.out.println("  They can now login from the Main Menu → Admin Login.");
        }
        InputUtil.pause();
    }

    private static void viewAllAdmins() {
        InputUtil.printHeader("ALL ADMINS");
        List<Admin> admins = new AdminDAO().getAllAdmins();
        if (admins.isEmpty()) {
            System.out.println("  No admins found.");
            InputUtil.pause();
            return;
        }
        System.out.printf("  %-5s %-15s %-25s %-12s %-10s %-20s%n",
                "ID", "Username", "Email", "Mobile", "Status", "Last Login");
        System.out.println("  " + "─".repeat(90));
        for (Admin a : admins) {
            System.out.printf("  %-5d %-15s %-25s %-12s %-10s %-20s%n",
                    a.getAdminId(),
                    a.getUsername(),
                    a.getEmail(),
                    a.getMobile(),
                    a.isActive() ? "Active" : "Inactive",
                    a.getLastLogin() != null ? a.getLastLogin().toLocalDate().toString() : "Never");
        }
        System.out.println("\n  Total Admins: " + admins.size());
        InputUtil.pause();
    }

    private static void deactivateAdmin() {
        InputUtil.printHeader("DEACTIVATE ADMIN");
        viewAllAdmins();
        int id = InputUtil.readInt("  Enter Admin ID to deactivate: ");
        if (id == currentAdmin.getAdminId()) {
            System.out.println("  [!] You cannot deactivate your own account.");
            InputUtil.pause();
            return;
        }
        if (!InputUtil.readConfirm("  Are you sure you want to deactivate Admin ID " + id + "?")) {
            System.out.println("  Cancelled.");
            InputUtil.pause();
            return;
        }
        if (new AdminDAO().deactivateAdmin(id)) {
            System.out.println("  ✔ Admin deactivated successfully.");
            auditLogDAO.addLog(new AuditLog("Admin", currentAdmin.getAdminId(), currentAdmin.getUsername(),
                    "ADMIN_DEACTIVATE", "Deactivated admin ID: " + id));
        } else {
            System.out.println("  [!] Deactivation failed. Check the Admin ID.");
        }
        InputUtil.pause();
    }

    // ── Admin: Manage Users ───────────────────────────────────
    private static void adminManageUsers() {
        InputUtil.printHeader("MANAGE USERS");
        System.out.println("  1. View All Users");
        System.out.println("  2. Back");
        int choice = InputUtil.readIntInRange("  Enter choice: ", 1, 2);
        if (choice == 1) {
            List<User> users = userDAO.getAllUsers();
            System.out.printf("%n  %-5s %-15s %-25s %-12s %-15s%n", "ID", "Username", "Email", "Mobile", "Joined");
            System.out.println("  " + "─".repeat(75));
            for (User u : users) {
                System.out.printf("  %-5d %-15s %-25s %-12s %-15s%n",
                        u.getUserId(), u.getUsername(), u.getEmail(),
                        u.getMobile(), u.getCreatedAt() != null ? u.getCreatedAt().toLocalDate() : "N/A");
            }
            System.out.println("\n  Total Users: " + users.size());
        }
        InputUtil.pause();
    }

    // ── Admin: Manage Articles ────────────────────────────────
    private static void adminManageArticles() {
        boolean back = false;
        while (!back) {
            InputUtil.printHeader("MANAGE NEWS ARTICLES");
            System.out.println("  1. View All Articles");
            System.out.println("  2. Search Articles");
            System.out.println("  3. Delete Article");
            System.out.println("  4. Run AI Analysis on Article");
            System.out.println("  5. Back");
            int choice = InputUtil.readIntInRange("  Enter choice: ", 1, 5);
            switch (choice) {
                case 1: printArticleList(articleDAO.getAllArticles()); InputUtil.pause(); break;
                case 2:
                    String kw = InputUtil.readRequiredString("  Search keyword: ");
                    printArticleList(articleDAO.searchArticles(kw));
                    InputUtil.pause();
                    break;
                case 3:
                    int delId = InputUtil.readInt("  Enter Article ID to delete: ");
                    if (articleDAO.deleteArticle(delId))
                        System.out.println("  Article deleted successfully.");
                    else
                        System.out.println("  [!] Article not found or already deleted.");
                    InputUtil.pause();
                    break;
                case 4: runAnalysisOnArticle(); break;
                case 5: back = true; break;
            }
        }
    }

    // ── Admin: Manage Posts ───────────────────────────────────
    private static void adminManagePosts() {
        boolean back = false;
        while (!back) {
            InputUtil.printHeader("MANAGE SOCIAL POSTS");
            System.out.println("  1. View All Posts");
            System.out.println("  2. Search Posts");
            System.out.println("  3. Delete Post");
            System.out.println("  4. Run AI Analysis on Post");
            System.out.println("  5. Back");
            int choice = InputUtil.readIntInRange("  Enter choice: ", 1, 5);
            switch (choice) {
                case 1: printPostList(postDAO.getAllPosts()); InputUtil.pause(); break;
                case 2:
                    String kw = InputUtil.readRequiredString("  Search keyword: ");
                    printPostList(postDAO.searchPosts(kw));
                    InputUtil.pause();
                    break;
                case 3:
                    int delId = InputUtil.readInt("  Enter Post ID to delete: ");
                    if (postDAO.deletePost(delId))
                        System.out.println("  Post deleted successfully.");
                    else
                        System.out.println("  [!] Post not found.");
                    InputUtil.pause();
                    break;
                case 4: runAnalysisOnPost(); break;
                case 5: back = true; break;
            }
        }
    }

    // ── Admin: Manage Sources ─────────────────────────────────
    private static void adminManageSources() {
        boolean back = false;
        while (!back) {
            InputUtil.printHeader("MANAGE NEWS SOURCES");
            System.out.println("  1. View All Sources");
            System.out.println("  2. Add Source");
            System.out.println("  3. Update Source");
            System.out.println("  4. Delete Source");
            System.out.println("  5. Recalculate Reliability");
            System.out.println("  6. Back");
            int choice = InputUtil.readIntInRange("  Enter choice: ", 1, 6);
            switch (choice) {
                case 1: printSourceList(sourceDAO.getAllSources()); InputUtil.pause(); break;
                case 2: addSource(); break;
                case 3: updateSource(); break;
                case 4:
                    int delId = InputUtil.readInt("  Enter Source ID to delete: ");
                    if (sourceDAO.deleteSource(delId))
                        System.out.println("  Source deleted.");
                    else
                        System.out.println("  [!] Delete failed (source may be linked to articles).");
                    InputUtil.pause();
                    break;
                case 5:
                    int srcId = InputUtil.readInt("  Enter Source ID to recalculate: ");
                    sourceDAO.recalculateReliability(srcId);
                    System.out.println("  Reliability score recalculated.");
                    InputUtil.pause();
                    break;
                case 6: back = true; break;
            }
        }
    }

    private static void addSource() {
        InputUtil.printHeader("ADD NEWS SOURCE");
        String name = InputUtil.readRequiredString("  Source Name : ");
        String url  = InputUtil.readRequiredString("  Source URL  : ");
        if (!ValidationUtil.isValidUrl(url)) { System.out.println("  [!] " + ValidationUtil.getUrlError(url)); InputUtil.pause(); return; }
        System.out.println("  Reliability Level:");
        System.out.println("  1. Very Reliable  2. Reliable  3. Moderate  4. Unverified  5. Unreliable");
        int lvl = InputUtil.readIntInRange("  Choose: ", 1, 5);
        String[] levels = {"Very Reliable", "Reliable", "Moderate", "Unverified", "Unreliable"};
        double[] scores = {90.0, 75.0, 55.0, 30.0, 10.0};
        NewsSource src = new NewsSource();
        src.setSourceName(name);
        src.setSourceUrl(url);
        src.setReliabilityLevel(levels[lvl - 1]);
        src.setReliabilityScore(scores[lvl - 1]);
        src.setAddedByAdmin(currentAdmin.getAdminId());
        int id = sourceDAO.addSource(src);
        if (id > 0) System.out.println("  Source added successfully! ID: " + id);
        else        System.out.println("  [!] Failed to add source.");
        InputUtil.pause();
    }

    private static void updateSource() {
        int id = InputUtil.readInt("  Enter Source ID to update: ");
        NewsSource src = sourceDAO.getSourceById(id);
        if (src == null) { System.out.println("  [!] Source not found."); InputUtil.pause(); return; }
        System.out.println("  Current Name  : " + src.getSourceName());
        System.out.println("  Current Level : " + src.getReliabilityLevel());
        String newName = InputUtil.readRequiredString("  New Name (Enter to keep current): ");
        if (!newName.isEmpty()) src.setSourceName(newName);
        String newUrl = InputUtil.readRequiredString("  New URL: ");
        if (!newUrl.isEmpty() && ValidationUtil.isValidUrl(newUrl)) src.setSourceUrl(newUrl);
        System.out.println("  Reliability Level: 1.Very Reliable 2.Reliable 3.Moderate 4.Unverified 5.Unreliable");
        int lvl = InputUtil.readIntInRange("  Choose: ", 1, 5);
        String[] levels = {"Very Reliable", "Reliable", "Moderate", "Unverified", "Unreliable"};
        double[] scores = {90.0, 75.0, 55.0, 30.0, 10.0};
        src.setReliabilityLevel(levels[lvl - 1]);
        src.setReliabilityScore(scores[lvl - 1]);
        if (sourceDAO.updateSource(src)) System.out.println("  Source updated successfully.");
        else                             System.out.println("  [!] Update failed.");
        InputUtil.pause();
    }

    // ── Admin: View Analysis ──────────────────────────────────
    private static void adminViewAnalysis() {
        InputUtil.printHeader("CREDIBILITY ANALYSIS");
        System.out.println("  1. Analysis for Article");
        System.out.println("  2. Analysis for Post");
        System.out.println("  3. Back");
        int choice = InputUtil.readIntInRange("  Enter choice: ", 1, 3);
        if (choice == 1) {
            int id = InputUtil.readInt("  Enter Article ID: ");
            CredibilityAnalysis a = analysisService.getArticleAnalysis(id);
            if (a != null) analysisService.printAnalysisResult(a);
            else           System.out.println("  No analysis found. Run AI analysis first.");
        } else if (choice == 2) {
            int id = InputUtil.readInt("  Enter Post ID: ");
            CredibilityAnalysis a = analysisService.getPostAnalysis(id);
            if (a != null) analysisService.printAnalysisResult(a);
            else           System.out.println("  No analysis found.");
        }
        InputUtil.pause();
    }

    // ── Admin: View Fact Checks ───────────────────────────────
    private static void adminViewFactChecks() {
        InputUtil.printHeader("FACT CHECKS");
        System.out.println("  1. All Fact Checks");
        System.out.println("  2. By Verdict");
        System.out.println("  3. Back");
        int choice = InputUtil.readIntInRange("  Enter choice: ", 1, 3);
        if (choice == 1) {
            factCheckService.getAllFactChecks().forEach(factCheckService::printFactCheck);
        } else if (choice == 2) {
            System.out.println("  Verdicts: True / Mostly True / Partially True / Misleading / False");
            String verdict = InputUtil.readRequiredString("  Enter verdict: ");
            new FactCheckDAO().getFactChecksByVerdict(verdict).forEach(factCheckService::printFactCheck);
        }
        InputUtil.pause();
    }

    // ── Admin: Manage Alerts ──────────────────────────────────
    private static void adminManageAlerts() {
        boolean back = false;
        while (!back) {
            InputUtil.printHeader("MANAGE ALERTS");
            System.out.println("  1. View All Alerts");
            System.out.println("  2. View Unresolved Alerts");
            System.out.println("  3. View Alerts by Level");
            System.out.println("  4. Resolve Alert");
            System.out.println("  5. Back");
            int choice = InputUtil.readIntInRange("  Enter choice: ", 1, 5);
            switch (choice) {
                case 1:
                    alertService.printAlertList(alertService.getAllAlerts(), "ALL ALERTS");
                    InputUtil.pause(); break;
                case 2:
                    alertService.printAlertList(alertService.getUnresolvedAlerts(), "UNRESOLVED ALERTS");
                    InputUtil.pause(); break;
                case 3:
                    System.out.println("  Levels: Low / Medium / High / Critical");
                    String level = InputUtil.readRequiredString("  Enter level: ");
                    alertService.printAlertList(alertService.getAlertsByLevel(level), level.toUpperCase() + " ALERTS");
                    InputUtil.pause(); break;
                case 4:
                    int alertId = InputUtil.readInt("  Enter Alert ID to resolve: ");
                    if (alertService.resolveAlert(alertId, currentAdmin))
                        System.out.println("  Alert resolved successfully.");
                    else
                        System.out.println("  [!] Failed to resolve alert.");
                    InputUtil.pause(); break;
                case 5: back = true; break;
            }
        }
    }

    // ── Admin: Spread Tracking ────────────────────────────────
    private static void adminViewSpread() {
        InputUtil.printHeader("SPREAD TRACKING");
        System.out.println("  1. View All Spread Events");
        System.out.println("  2. Spread Timeline for Article");
        System.out.println("  3. Spread Timeline for Post");
        System.out.println("  4. Back");
        int choice = InputUtil.readIntInRange("  Enter choice: ", 1, 4);
        switch (choice) {
            case 1:
                spreadService.printSpreadTimeline(spreadService.getAllSpreadEvents());
                break;
            case 2:
                int aid = InputUtil.readInt("  Enter Article ID: ");
                spreadService.printSpreadTimeline(spreadService.getSpreadTimelineForArticle(aid));
                System.out.println("  Total Reach: " + String.format("%,d", spreadService.getTotalReach(aid)));
                break;
            case 3:
                int pid = InputUtil.readInt("  Enter Post ID: ");
                spreadService.printSpreadTimeline(spreadService.getSpreadTimelineForPost(pid));
                break;
        }
        InputUtil.pause();
    }

    // ── Admin: Reports ────────────────────────────────────────
    private static void adminReports() {
        boolean back = false;
        while (!back) {
            InputUtil.printHeader("REPORTS");
            System.out.println("  1. Summary Report");
            System.out.println("  2. Source Reliability Report");
            System.out.println("  3. Viral Spread Report");
            System.out.println("  4. Platform-wise Misinformation Report");
            System.out.println("  5. Monthly Analysis Report");
            System.out.println("  6. High Risk Articles Report");
            System.out.println("  7. Audit Log Report");
            System.out.println("  8. Back");
            int choice = InputUtil.readIntInRange("  Enter choice: ", 1, 8);
            switch (choice) {
                case 1: reportService.printSummaryReport(currentAdmin);           break;
                case 2: reportService.printSourceReliabilityReport(currentAdmin); break;
                case 3: reportService.printViralSpreadReport(currentAdmin);       break;
                case 4: reportService.printPlatformMisinfoReport(currentAdmin);   break;
                case 5: reportService.printMonthlyAnalysisReport(currentAdmin);   break;
                case 6: reportService.printHighRiskArticlesReport(currentAdmin);  break;
                case 7: reportService.printAuditLogReport(currentAdmin);          break;
                case 8: back = true; break;
            }
            if (choice != 8) InputUtil.pause();
        }
    }

    // ── Admin: Change Password ────────────────────────────────
    private static void adminChangePassword() {
        InputUtil.printHeader("CHANGE ADMIN PASSWORD");
        String oldPass = InputUtil.readPassword("  Current Password : ");
        String newPass = InputUtil.readPassword("  New Password     : ");
        String confirm = InputUtil.readPassword("  Confirm Password : ");
        if (!newPass.equals(confirm)) {
            System.out.println("  [!] Passwords do not match.");
        } else if (authService.adminChangePassword(currentAdmin, oldPass, newPass)) {
            System.out.println("  Password changed successfully.");
        } else {
            System.out.println("  [!] Failed. Check current password or password rules.");
        }
        InputUtil.pause();
    }

    // ─────────────────────────────────────────────────────────
    // USER LOGIN & REGISTER FLOW
    // ─────────────────────────────────────────────────────────
    private static void userLoginFlow() {
        InputUtil.printHeader("USER LOGIN");
        String username = InputUtil.readRequiredString("  Username : ");
        String password = InputUtil.readPassword("  Password : ");
        currentUser = authService.userLogin(username, password);
        if (currentUser == null) {
            System.out.println("\n  [!] Invalid credentials or account inactive.");
            InputUtil.pause();
            return;
        }
        System.out.println("\n  Welcome, " + currentUser.getFullName() + "!");
        userDashboard();
        currentUser = null;
    }

    private static void userRegisterFlow() {
        InputUtil.printHeader("USER REGISTRATION");
        String username = InputUtil.readRequiredString("  Username (5-20 chars)       : ");
        String fullName = InputUtil.readRequiredString("  Full Name (alphabets only)  : ");
        String email    = InputUtil.readRequiredString("  Email                       : ");
        String mobile   = InputUtil.readRequiredString("  Mobile (10 digits)          : ");
        String password = InputUtil.readPassword(      "  Password (min 8, A-Z,a-z,0-9,@#$): ");
        String confirm  = InputUtil.readPassword(      "  Confirm Password            : ");

        if (!password.equals(confirm)) {
            System.out.println("\n  [!] Passwords do not match.");
            InputUtil.pause();
            return;
        }

        String error = authService.registerUser(username, password, fullName, email, mobile);
        if (error != null) {
            System.out.println("\n  [!] Registration failed: " + error);
        } else {
            System.out.println("\n  ✔ Registration successful! You can now login.");
        }
        InputUtil.pause();
    }

    // ─────────────────────────────────────────────────────────
    // USER DASHBOARD
    // ─────────────────────────────────────────────────────────
    private static void userDashboard() {
        boolean active = true;
        while (active) {
            InputUtil.printHeader("USER DASHBOARD — " + currentUser.getUsername());
            System.out.println("  1.  My Profile");
            System.out.println("  2.  Submit News Article");
            System.out.println("  3.  Manage My Articles");
            System.out.println("  4.  Submit Social Media Post");
            System.out.println("  5.  Manage My Posts");
            System.out.println("  6.  Run AI Analysis");
            System.out.println("  7.  View Analysis Results");
            System.out.println("  8.  Fact Check Content");
            System.out.println("  9.  Record Spread Event");
            System.out.println("  10. View My Alerts");
            System.out.println("  11. Change Password");
            System.out.println("  12. Delete My Account");
            System.out.println("  13. Logout");
            InputUtil.printDivider();
            int choice = InputUtil.readIntInRange("  Enter choice: ", 1, 13);
            switch (choice) {
                case 1:  viewProfile();           break;
                case 2:  submitArticle();         break;
                case 3:  manageMyArticles();      break;
                case 4:  submitPost();            break;
                case 5:  manageMyPosts();         break;
                case 6:  runAIAnalysisMenu();     break;
                case 7:  viewMyAnalysisResults(); break;
                case 8:  factCheckMenu();         break;
                case 9:  recordSpreadEvent();     break;
                case 10: viewMyAlerts();          break;
                case 11: userChangePassword();    break;
                case 12:
                    if (InputUtil.readConfirm("  Are you sure you want to delete your account?")) {
                        authService.deleteAccount(currentUser);
                        System.out.println("  Account deleted. Goodbye!");
                        active = false;
                    }
                    break;
                case 13:
                    authService.userLogout(currentUser);
                    active = false;
                    break;
            }
        }
    }

    // ── User: Profile ─────────────────────────────────────────
    private static void viewProfile() {
        InputUtil.printHeader("MY PROFILE");
        User u = currentUser;
        System.out.println("  User ID   : " + u.getUserId());
        System.out.println("  Username  : " + u.getUsername());
        System.out.println("  Full Name : " + u.getFullName());
        System.out.println("  Email     : " + u.getEmail());
        System.out.println("  Mobile    : " + u.getMobile());
        System.out.println("  Member Since : " + (u.getCreatedAt() != null ? u.getCreatedAt().toLocalDate() : "N/A"));
        System.out.println("  Last Login   : " + (u.getLastLogin() != null ? u.getLastLogin() : "N/A"));
        System.out.println();
        if (InputUtil.readConfirm("  Update profile?")) {
            String name   = InputUtil.readRequiredString("  New Full Name  : ");
            String email  = InputUtil.readRequiredString("  New Email      : ");
            String mobile = InputUtil.readRequiredString("  New Mobile     : ");
            currentUser.setFullName(name);
            currentUser.setEmail(email);
            currentUser.setMobile(mobile);
            if (authService.updateProfile(currentUser))
                System.out.println("  Profile updated successfully.");
            else
                System.out.println("  [!] Update failed. Check the values entered.");
        }
        InputUtil.pause();
    }

    // ── User: Submit Article ──────────────────────────────────
    private static void submitArticle() {
        InputUtil.printHeader("SUBMIT NEWS ARTICLE");

        String title = InputUtil.readRequiredString("  Article Title (min 5 chars) : ");
        String err = ValidationUtil.getTitleError(title);
        if (err != null) { System.out.println("  [!] " + err); InputUtil.pause(); return; }

        String content = InputUtil.readMultiLine("  Article Content (min 100 chars):");
        err = ValidationUtil.getContentError(content);
        if (err != null) { System.out.println("  [!] " + err); InputUtil.pause(); return; }

        String author     = InputUtil.readRequiredString("  Author Name           : ");
        String sourceName = InputUtil.readRequiredString("  Source Name           : ");
        String sourceUrl  = InputUtil.readRequiredString("  Source URL            : ");
        if (!ValidationUtil.isValidUrl(sourceUrl)) {
            System.out.println("  [!] " + ValidationUtil.getUrlError(sourceUrl));
            InputUtil.pause(); return;
        }

        System.out.println("  Category: 1.Politics 2.Health 3.Science 4.Technology 5.Business 6.Sports 7.Entertainment 8.World 9.Other");
        int catChoice = InputUtil.readIntInRange("  Choose category: ", 1, 9);
        String[] cats = {"Politics","Health","Science","Technology","Business","Sports","Entertainment","World","Other"};

        LocalDate pubDate = InputUtil.readDate("  Publication Date");
        err = ValidationUtil.getPublicationDateError(pubDate);
        if (err != null) { System.out.println("  [!] " + err); InputUtil.pause(); return; }

        NewsArticle article = new NewsArticle();
        article.setTitle(title);
        article.setContent(content);
        article.setAuthor(author);
        article.setSourceName(sourceName);
        article.setSourceUrl(sourceUrl);
        article.setCategory(cats[catChoice - 1]);
        article.setPublicationDate(pubDate);
        article.setSubmittedBy(currentUser.getUserId());

        int id = articleDAO.addArticle(article);
        if (id > 0) {
            System.out.println("\n  ✔ Article submitted successfully! Article ID: " + id);
            auditLogDAO.addLog(new AuditLog("User", currentUser.getUserId(), currentUser.getUsername(),
                    "ARTICLE_SUBMIT", "Submitted article ID: " + id));
            if (InputUtil.readConfirm("  Run AI credibility analysis now?")) {
                article.setArticleId(id);
                CredibilityAnalysis result = analysisService.analyzeArticle(article, currentUser);
                if (result != null) analysisService.printAnalysisResult(result);
            }
        } else {
            System.out.println("  [!] Failed to submit article.");
        }
        InputUtil.pause();
    }

    // ── User: Manage My Articles ──────────────────────────────
    private static void manageMyArticles() {
        boolean back = false;
        while (!back) {
            InputUtil.printHeader("MY ARTICLES");
            System.out.println("  1. View My Articles");
            System.out.println("  2. Update Article");
            System.out.println("  3. Delete Article");
            System.out.println("  4. Back");
            int choice = InputUtil.readIntInRange("  Enter choice: ", 1, 4);
            switch (choice) {
                case 1:
                    printArticleList(articleDAO.getArticlesByUser(currentUser.getUserId()));
                    InputUtil.pause(); break;
                case 2: updateMyArticle();   break;
                case 3:
                    int delId = InputUtil.readInt("  Enter Article ID to delete: ");
                    NewsArticle check = articleDAO.getArticleById(delId);
                    if (check == null || check.getSubmittedBy() != currentUser.getUserId()) {
                        System.out.println("  [!] Article not found or not yours.");
                    } else if (articleDAO.deleteArticle(delId)) {
                        System.out.println("  Article deleted.");
                        auditLogDAO.addLog(new AuditLog("User", currentUser.getUserId(), currentUser.getUsername(),
                                "ARTICLE_DELETE", "Deleted article ID: " + delId));
                    } else {
                        System.out.println("  [!] Delete failed.");
                    }
                    InputUtil.pause(); break;
                case 4: back = true; break;
            }
        }
    }

    private static void updateMyArticle() {
        int id = InputUtil.readInt("  Enter Article ID to update: ");
        NewsArticle article = articleDAO.getArticleById(id);
        if (article == null || article.getSubmittedBy() != currentUser.getUserId()) {
            System.out.println("  [!] Article not found or not yours.");
            InputUtil.pause(); return;
        }
        System.out.println("  Current Title: " + article.getTitle());
        String newTitle = InputUtil.readRequiredString("  New Title (Enter to skip): ");
        if (!newTitle.isEmpty()) article.setTitle(newTitle);

        System.out.println("  Current Author: " + article.getAuthor());
        String newAuthor = InputUtil.readRequiredString("  New Author: ");
        if (!newAuthor.isEmpty()) article.setAuthor(newAuthor);

        String newSourceName = InputUtil.readRequiredString("  New Source Name: ");
        if (!newSourceName.isEmpty()) article.setSourceName(newSourceName);

        String newSourceUrl = InputUtil.readRequiredString("  New Source URL: ");
        if (!newSourceUrl.isEmpty() && ValidationUtil.isValidUrl(newSourceUrl))
            article.setSourceUrl(newSourceUrl);

        if (articleDAO.updateArticle(article)) {
            System.out.println("  ✔ Article updated successfully.");
            auditLogDAO.addLog(new AuditLog("User", currentUser.getUserId(), currentUser.getUsername(),
                    "ARTICLE_UPDATE", "Updated article ID: " + id));
        } else {
            System.out.println("  [!] Update failed.");
        }
        InputUtil.pause();
    }

    // ── User: Submit Post ─────────────────────────────────────
    private static void submitPost() {
        InputUtil.printHeader("SUBMIT SOCIAL MEDIA POST");

        System.out.println("  Platform: 1.Facebook  2.X(Twitter)  3.Instagram  4.YouTube  5.Reddit");
        int platChoice = InputUtil.readIntInRange("  Choose platform: ", 1, 5);
        String[] platforms = {"Facebook", "X(Twitter)", "Instagram", "YouTube", "Reddit"};

        String content = InputUtil.readRequiredString("  Post Content : ");
        String err = ValidationUtil.getPostContentError(content);
        if (err != null) { System.out.println("  [!] " + err); InputUtil.pause(); return; }

        String author  = InputUtil.readRequiredString("  Author/Handle : ");
        String postUrl = InputUtil.readRequiredString("  Post URL      : ");
        if (!ValidationUtil.isValidUrl(postUrl)) {
            System.out.println("  [!] " + ValidationUtil.getUrlError(postUrl));
            InputUtil.pause(); return;
        }

        LocalDate postDate = InputUtil.readDate("  Post Date");

        SocialPost post = new SocialPost();
        post.setPlatform(platforms[platChoice - 1]);
        post.setContent(content);
        post.setAuthor(author);
        post.setPostUrl(postUrl);
        post.setPostDate(postDate);
        post.setSubmittedBy(currentUser.getUserId());

        int id = postDAO.addPost(post);
        if (id > 0) {
            System.out.println("\n  ✔ Post submitted! Post ID: " + id);
            auditLogDAO.addLog(new AuditLog("User", currentUser.getUserId(), currentUser.getUsername(),
                    "POST_SUBMIT", "Submitted post ID: " + id));
            if (InputUtil.readConfirm("  Run AI credibility analysis now?")) {
                post.setPostId(id);
                CredibilityAnalysis result = analysisService.analyzePost(post, currentUser);
                if (result != null) analysisService.printAnalysisResult(result);
            }
        } else {
            System.out.println("  [!] Failed to submit post.");
        }
        InputUtil.pause();
    }

    // ── User: Manage My Posts ─────────────────────────────────
    private static void manageMyPosts() {
        boolean back = false;
        while (!back) {
            InputUtil.printHeader("MY POSTS");
            System.out.println("  1. View My Posts");
            System.out.println("  2. Delete Post");
            System.out.println("  3. Back");
            int choice = InputUtil.readIntInRange("  Enter choice: ", 1, 3);
            switch (choice) {
                case 1: printPostList(postDAO.getPostsByUser(currentUser.getUserId())); InputUtil.pause(); break;
                case 2:
                    int delId = InputUtil.readInt("  Enter Post ID to delete: ");
                    SocialPost chk = postDAO.getPostById(delId);
                    if (chk == null || chk.getSubmittedBy() != currentUser.getUserId()) {
                        System.out.println("  [!] Post not found or not yours.");
                    } else if (postDAO.deletePost(delId)) {
                        System.out.println("  Post deleted.");
                    } else {
                        System.out.println("  [!] Delete failed.");
                    }
                    InputUtil.pause(); break;
                case 3: back = true; break;
            }
        }
    }

    // ── User: AI Analysis Menu ────────────────────────────────
    private static void runAIAnalysisMenu() {
        InputUtil.printHeader("RUN AI ANALYSIS");
        System.out.println("  1. Analyze Article");
        System.out.println("  2. Analyze Social Post");
        System.out.println("  3. Back");
        int choice = InputUtil.readIntInRange("  Enter choice: ", 1, 3);
        if (choice == 1)      runAnalysisOnArticle();
        else if (choice == 2) runAnalysisOnPost();
    }

    private static void runAnalysisOnArticle() {
        // Use admin or user context depending on who is logged in
        User actingUser = currentUser != null ? currentUser :
                new User("admin", "", currentAdmin != null ? currentAdmin.getFullName() : "Admin",
                        "", "");
        if (currentAdmin != null) {
            actingUser.setUserId(currentAdmin.getAdminId());
            actingUser.setUsername(currentAdmin.getUsername());
        }

        int id = InputUtil.readInt("  Enter Article ID to analyze: ");
        NewsArticle article = articleDAO.getArticleById(id);
        if (article == null) { System.out.println("  [!] Article not found."); InputUtil.pause(); return; }
        System.out.println("  Analyzing: " + article.getTitle());
        System.out.println("  Please wait...");
        CredibilityAnalysis result = analysisService.analyzeArticle(article, actingUser);
        if (result != null) analysisService.printAnalysisResult(result);
        InputUtil.pause();
    }

    private static void runAnalysisOnPost() {
        User actingUser = currentUser != null ? currentUser :
                new User("admin", "", currentAdmin != null ? currentAdmin.getFullName() : "Admin", "", "");
        if (currentAdmin != null) {
            actingUser.setUserId(currentAdmin.getAdminId());
            actingUser.setUsername(currentAdmin.getUsername());
        }

        int id = InputUtil.readInt("  Enter Post ID to analyze: ");
        SocialPost post = postDAO.getPostById(id);
        if (post == null) { System.out.println("  [!] Post not found."); InputUtil.pause(); return; }
        System.out.println("  Analyzing post by: " + post.getAuthor());
        System.out.println("  Please wait...");
        CredibilityAnalysis result = analysisService.analyzePost(post, actingUser);
        if (result != null) analysisService.printAnalysisResult(result);
        InputUtil.pause();
    }

    // ── User: View Analysis Results ───────────────────────────
    private static void viewMyAnalysisResults() {
        InputUtil.printHeader("MY ANALYSIS RESULTS");
        System.out.println("  1. For Article");
        System.out.println("  2. For Post");
        System.out.println("  3. Back");
        int choice = InputUtil.readIntInRange("  Enter choice: ", 1, 3);
        if (choice == 1) {
            List<NewsArticle> articles = articleDAO.getArticlesByUser(currentUser.getUserId());
            if (articles.isEmpty()) { System.out.println("  No articles found."); InputUtil.pause(); return; }
            printArticleList(articles);
            int id = InputUtil.readInt("  Enter Article ID to view analysis: ");
            CredibilityAnalysis a = analysisService.getArticleAnalysis(id);
            if (a != null) analysisService.printAnalysisResult(a);
            else           System.out.println("  No analysis found. Please run AI analysis first.");
        } else if (choice == 2) {
            List<SocialPost> posts = postDAO.getPostsByUser(currentUser.getUserId());
            if (posts.isEmpty()) { System.out.println("  No posts found."); InputUtil.pause(); return; }
            printPostList(posts);
            int id = InputUtil.readInt("  Enter Post ID to view analysis: ");
            CredibilityAnalysis a = analysisService.getPostAnalysis(id);
            if (a != null) analysisService.printAnalysisResult(a);
            else           System.out.println("  No analysis found.");
        }
        InputUtil.pause();
    }

    // ── User: Fact Check ──────────────────────────────────────
    private static void factCheckMenu() {
        InputUtil.printHeader("FACT CHECK CONTENT");
        System.out.println("  1. Add Fact Check for Article");
        System.out.println("  2. Add Fact Check for Social Post");
        System.out.println("  3. View Fact Checks for Article");
        System.out.println("  4. View Fact Checks for Post");
        System.out.println("  5. Back");
        int choice = InputUtil.readIntInRange("  Enter choice: ", 1, 5);
        switch (choice) {
            case 1: addFactCheck("Article"); break;
            case 2: addFactCheck("Post");    break;
            case 3:
                int aid = InputUtil.readInt("  Enter Article ID: ");
                factCheckService.getFactChecksForArticle(aid).forEach(factCheckService::printFactCheck);
                InputUtil.pause(); break;
            case 4:
                int pid = InputUtil.readInt("  Enter Post ID: ");
                factCheckService.getFactChecksForPost(pid).forEach(factCheckService::printFactCheck);
                InputUtil.pause(); break;
            case 5: break;
        }
    }

    private static void addFactCheck(String contentType) {
        InputUtil.printHeader("ADD FACT CHECK — " + contentType);
        FactCheck fc = new FactCheck();
        fc.setContentType(contentType);
        if (contentType.equals("Article")) {
            fc.setArticleId(InputUtil.readInt("  Enter Article ID: "));
        } else {
            fc.setPostId(InputUtil.readInt("  Enter Post ID: "));
        }
        fc.setClaim(InputUtil.readRequiredString("  Claim to verify: "));
        System.out.println("  Verdict: 1.True  2.Mostly True  3.Partially True  4.Misleading  5.False");
        int v = InputUtil.readIntInRange("  Choose verdict: ", 1, 5);
        String[] verdicts = {"True", "Mostly True", "Partially True", "Misleading", "False"};
        fc.setVerdict(verdicts[v - 1]);
        fc.setExplanation(InputUtil.readRequiredString("  Explanation: "));
        fc.setReferenceUrls(InputUtil.readString("  Reference URLs (optional): "));

        if (factCheckService.addFactCheck(fc, currentUser)) {
            System.out.println("  ✔ Fact check added successfully.");
            auditLogDAO.addLog(new AuditLog("User", currentUser.getUserId(), currentUser.getUsername(),
                    "FACT_CHECK", "Added fact check for " + contentType));
        } else {
            System.out.println("  [!] Failed to add fact check.");
        }
        InputUtil.pause();
    }

    // ── User: Record Spread Event ─────────────────────────────
    private static void recordSpreadEvent() {
        InputUtil.printHeader("RECORD SPREAD EVENT");
        System.out.println("  Content Type: 1.Article  2.Post");
        int typeChoice = InputUtil.readIntInRange("  Choose: ", 1, 2);

        SpreadTracking st = new SpreadTracking();
        if (typeChoice == 1) {
            st.setContentType("Article");
            st.setArticleId(InputUtil.readInt("  Enter Article ID: "));
        } else {
            st.setContentType("Post");
            st.setPostId(InputUtil.readInt("  Enter Post ID: "));
        }

        st.setOriginalSource(InputUtil.readRequiredString("  Original Source   : "));
        st.setRepostedBy(InputUtil.readRequiredString(    "  Reposted By       : "));

        System.out.println("  Platform: 1.Facebook 2.X(Twitter) 3.Instagram 4.YouTube 5.Reddit 6.News Website 7.Other");
        int platChoice = InputUtil.readIntInRange("  Choose: ", 1, 7);
        String[] platforms = {"Facebook","X(Twitter)","Instagram","YouTube","Reddit","News Website","Other"};
        st.setPlatform(platforms[platChoice - 1]);

        st.setSpreadDate(InputUtil.readDate("  Spread Date"));
        st.setReachCount(InputUtil.readInt("  Reach Count (approx): "));
        st.setSpreadNotes(InputUtil.readString("  Notes (optional): "));

        if (spreadService.recordSpreadEvent(st, currentUser)) {
            System.out.println("  ✔ Spread event recorded successfully.");
        } else {
            System.out.println("  [!] Failed to record spread event.");
        }
        InputUtil.pause();
    }

    // ── User: View Alerts ─────────────────────────────────────
    private static void viewMyAlerts() {
        InputUtil.printHeader("MY ALERTS");
        alertService.printAlertList(alertService.getAlertsForUser(currentUser.getUserId()), "YOUR CONTENT ALERTS");
        InputUtil.pause();
    }

    // ── User: Change Password ─────────────────────────────────
    private static void userChangePassword() {
        InputUtil.printHeader("CHANGE PASSWORD");
        String oldPass = InputUtil.readPassword("  Current Password : ");
        String newPass = InputUtil.readPassword("  New Password     : ");
        String confirm = InputUtil.readPassword("  Confirm Password : ");
        if (!newPass.equals(confirm)) {
            System.out.println("  [!] Passwords do not match.");
        } else if (authService.userChangePassword(currentUser, oldPass, newPass)) {
            System.out.println("  ✔ Password changed successfully.");
        } else {
            System.out.println("  [!] Failed. Check current password or password requirements.");
        }
        InputUtil.pause();
    }

    // ─────────────────────────────────────────────────────────
    // PRINT HELPERS
    // ─────────────────────────────────────────────────────────
    private static void printArticleList(List<NewsArticle> articles) {
        if (articles.isEmpty()) { System.out.println("  No articles found."); return; }
        System.out.printf("%n  %-5s  %-38s %-14s %-12s %-12s%n", "ID", "Title", "Category", "Source", "Date");
        System.out.println("  " + "─".repeat(85));
        for (NewsArticle a : articles) {
            String title = a.getTitle().length() > 38 ? a.getTitle().substring(0, 35) + "..." : a.getTitle();
            String src   = a.getSourceName() != null ? (a.getSourceName().length() > 14 ? a.getSourceName().substring(0, 11) + "..." : a.getSourceName()) : "N/A";
            System.out.printf("  %-5d  %-38s %-14s %-12s %-12s%n",
                    a.getArticleId(), title, a.getCategory(), src, a.getPublicationDate());
        }
        System.out.println("  Total: " + articles.size() + " article(s).");
    }

    private static void printPostList(List<SocialPost> posts) {
        if (posts.isEmpty()) { System.out.println("  No posts found."); return; }
        System.out.printf("%n  %-5s  %-14s %-35s %-15s %-12s%n", "ID", "Platform", "Content", "Author", "Date");
        System.out.println("  " + "─".repeat(85));
        for (SocialPost p : posts) {
            String content = p.getContent().length() > 35 ? p.getContent().substring(0, 32) + "..." : p.getContent();
            System.out.printf("  %-5d  %-14s %-35s %-15s %-12s%n",
                    p.getPostId(), p.getPlatform(), content, p.getAuthor(), p.getPostDate());
        }
        System.out.println("  Total: " + posts.size() + " post(s).");
    }

    private static void printSourceList(List<NewsSource> sources) {
        if (sources.isEmpty()) { System.out.println("  No sources found."); return; }
        System.out.printf("%n  %-5s  %-25s %-18s %8s %10s%n", "ID", "Name", "Level", "Score", "Articles");
        System.out.println("  " + "─".repeat(72));
        for (NewsSource s : sources) {
            System.out.printf("  %-5d  %-25s %-18s %8.1f %10d%n",
                    s.getSourceId(), s.getSourceName(), s.getReliabilityLevel(),
                    s.getReliabilityScore(), s.getTotalArticles());
        }
        System.out.println("  Total: " + sources.size() + " source(s).");
    }
}