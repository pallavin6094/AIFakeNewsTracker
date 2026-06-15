package com.fakenews.service;

import com.fakenews.dao.SpreadDAO;
import com.fakenews.dao.AuditLogDAO;
import com.fakenews.dao.AlertDAO;
import com.fakenews.model.*;

import java.util.List;

/**
 * SpreadService - Business logic for viral spread tracking.
 */
public class SpreadService {

    private final SpreadDAO   spreadDAO   = new SpreadDAO();
    private final AlertDAO    alertDAO    = new AlertDAO();
    private final AuditLogDAO auditLogDAO = new AuditLogDAO();

    private static final int VIRAL_THRESHOLD = 10000; // reach count alert threshold

    public boolean recordSpreadEvent(SpreadTracking st, User user) {
        if (st.getOriginalSource() == null || st.getOriginalSource().trim().isEmpty()) {
            System.out.println("  [!] Original source cannot be empty.");
            return false;
        }
        if (st.getSpreadDate() == null) {
            System.out.println("  [!] Spread date cannot be empty.");
            return false;
        }
        st.setRecordedBy(user.getUserId());
        int id = spreadDAO.addSpreadEvent(st);
        if (id > 0) {
            auditLogDAO.addLog(new AuditLog("User", user.getUserId(), user.getUsername(),
                    "SPREAD_RECORD", "Recorded spread event ID: " + id));

            // Auto-alert if reach count exceeds threshold
            if (st.getReachCount() >= VIRAL_THRESHOLD) {
                Alert alert = new Alert();
                alert.setContentType(st.getContentType());
                alert.setArticleId(st.getArticleId());
                alert.setPostId(st.getPostId());
                alert.setAlertType("Viral Spread");
                alert.setAlertLevel("High");
                alert.setAlertMessage(String.format(
                        "Content has reached %,d people via %s. Viral spread threshold exceeded.",
                        st.getReachCount(), st.getPlatform()));
                alertDAO.addAlert(alert);
            }
            return true;
        }
        return false;
    }

    public List<SpreadTracking> getSpreadTimelineForArticle(int articleId) {
        return spreadDAO.getSpreadByArticle(articleId);
    }

    public List<SpreadTracking> getSpreadTimelineForPost(int postId) {
        return spreadDAO.getSpreadByPost(postId);
    }

    public List<SpreadTracking> getAllSpreadEvents() {
        return spreadDAO.getAllSpreadEvents();
    }

    public int getTotalReach(int articleId) {
        return spreadDAO.getTotalReachByArticle(articleId);
    }

    public void printSpreadTimeline(List<SpreadTracking> events) {
        if (events.isEmpty()) { System.out.println("  No spread events recorded."); return; }
        System.out.println("\n  SPREAD TIMELINE:");
        System.out.println("  ─────────────────────────────────────────────────");
        for (SpreadTracking st : events) {
            System.out.printf("  [%s] %-15s → %-20s | Reach: %,d%n",
                    st.getSpreadDate(), st.getPlatform(), st.getRepostedBy(), st.getReachCount());
            System.out.println("       Origin: " + st.getOriginalSource());
        }
    }
}
