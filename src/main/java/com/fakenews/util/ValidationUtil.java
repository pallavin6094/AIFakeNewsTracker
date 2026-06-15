package com.fakenews.util;

import java.time.LocalDate;
import java.util.regex.Pattern;

/**
 * ValidationUtil - Centralized input validation for the entire application.
 */
public class ValidationUtil {

    private static final Pattern EMAIL_PATTERN    = Pattern.compile("^[a-zA-Z0-9+_.-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");
    private static final Pattern MOBILE_PATTERN   = Pattern.compile("^[0-9]{10}$");
    private static final Pattern NAME_PATTERN     = Pattern.compile("^[a-zA-Z ]+$");
    private static final Pattern URL_PATTERN      = Pattern.compile("^(https?|ftp)://[^\\s/$.?#].[^\\s]*$");
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=!]).{8,}$");

    // ── Username ──────────────────────────────────────────────
    public static boolean isValidUsername(String u) {
        return u != null && u.length() >= 5 && u.length() <= 20;
    }
    public static String getUsernameError(String u) {
        if (u == null || u.trim().isEmpty()) return "Username cannot be empty.";
        if (u.length() < 5)  return "Username must be at least 5 characters.";
        if (u.length() > 20) return "Username cannot exceed 20 characters.";
        return null;
    }

    // ── Name ─────────────────────────────────────────────────
    public static boolean isValidName(String n) {
        return n != null && !n.trim().isEmpty() && NAME_PATTERN.matcher(n.trim()).matches();
    }
    public static String getNameError(String n) {
        if (n == null || n.trim().isEmpty()) return "Name cannot be empty.";
        if (!NAME_PATTERN.matcher(n.trim()).matches()) return "Name must contain alphabets only.";
        return null;
    }

    // ── Email ─────────────────────────────────────────────────
    public static boolean isValidEmail(String e) {
        return e != null && EMAIL_PATTERN.matcher(e.trim()).matches();
    }
    public static String getEmailError(String e) {
        if (e == null || e.trim().isEmpty()) return "Email cannot be empty.";
        if (!EMAIL_PATTERN.matcher(e.trim()).matches()) return "Invalid email format.";
        return null;
    }

    // ── Mobile ───────────────────────────────────────────────
    public static boolean isValidMobile(String m) {
        return m != null && MOBILE_PATTERN.matcher(m.trim()).matches();
    }
    public static String getMobileError(String m) {
        if (m == null || m.trim().isEmpty()) return "Mobile number cannot be empty.";
        if (!MOBILE_PATTERN.matcher(m.trim()).matches()) return "Mobile must be exactly 10 digits.";
        return null;
    }

    // ── Password ─────────────────────────────────────────────
    public static boolean isValidPassword(String p) {
        return p != null && PASSWORD_PATTERN.matcher(p).matches();
    }
    public static String getPasswordError(String p) {
        if (p == null || p.isEmpty())          return "Password cannot be empty.";
        if (p.length() < 8)                    return "Password must be at least 8 characters.";
        if (!p.matches(".*[A-Z].*"))           return "Password must contain at least one uppercase letter.";
        if (!p.matches(".*[a-z].*"))           return "Password must contain at least one lowercase letter.";
        if (!p.matches(".*\\d.*"))             return "Password must contain at least one digit.";
        if (!p.matches(".*[@#$%^&+=!].*"))     return "Password must contain at least one special character (@#$%^&+=!).";
        return null;
    }

    // ── URL ──────────────────────────────────────────────────
    public static boolean isValidUrl(String url) {
        return url != null && URL_PATTERN.matcher(url.trim()).matches();
    }
    public static String getUrlError(String url) {
        if (url == null || url.trim().isEmpty()) return "URL cannot be empty.";
        if (!URL_PATTERN.matcher(url.trim()).matches()) return "Invalid URL. Must start with http:// or https://";
        return null;
    }

    // ── Article ───────────────────────────────────────────────
    public static String getTitleError(String title) {
        if (title == null || title.trim().isEmpty()) return "Title cannot be empty.";
        if (title.trim().length() < 5) return "Title must be at least 5 characters.";
        return null;
    }
    public static String getContentError(String content) {
        if (content == null || content.trim().isEmpty()) return "Content cannot be empty.";
        if (content.trim().length() < 100) return "Content must be at least 100 characters.";
        return null;
    }
    public static String getPublicationDateError(LocalDate date) {
        if (date == null) return "Publication date cannot be empty.";
        if (date.isAfter(LocalDate.now())) return "Publication date cannot be a future date.";
        return null;
    }

    // ── Post ─────────────────────────────────────────────────
    public static String getPostContentError(String content) {
        if (content == null || content.trim().isEmpty()) return "Post content cannot be empty.";
        return null;
    }
    public static boolean isValidPlatform(String platform) {
        return platform != null && (platform.equals("Facebook") || platform.equals("X(Twitter)") ||
               platform.equals("Instagram") || platform.equals("YouTube") || platform.equals("Reddit"));
    }

    // ── Helpers ───────────────────────────────────────────────
    public static boolean isNotEmpty(String value) {
        return value != null && !value.trim().isEmpty();
    }
    public static boolean isPositiveInt(int value) {
        return value > 0;
    }
}
