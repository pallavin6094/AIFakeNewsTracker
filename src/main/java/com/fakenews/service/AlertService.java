package com.fakenews.service;

import com.fakenews.dao.AlertDAO;
import com.fakenews.dao.AuditLogDAO;
import com.fakenews.model.Admin;
import com.fakenews.model.Alert;
import com.fakenews.model.AuditLog;

import java.util.List;

/**
 * AlertService - Business logic for alert management.
 */
public class AlertService {

    private final AlertDAO    alertDAO    = new AlertDAO();
    private final AuditLogDAO auditLogDAO = new AuditLogDAO();

    public List<Alert> getAllAlerts() {
        return alertDAO.getAllAlerts();
    }

    public List<Alert> getUnresolvedAlerts() {
        return alertDAO.getUnresolvedAlerts();
    }

    public List<Alert> getAlertsByLevel(String level) {
        return alertDAO.getAlertsByLevel(level);
    }

    public List<Alert> getAlertsForUser(int userId) {
        return alertDAO.getAlertsByUser(userId);
    }

    public boolean resolveAlert(int alertId, Admin admin) {
        boolean ok = alertDAO.resolveAlert(alertId, admin.getAdminId());
        if (ok) {
            auditLogDAO.addLog(new AuditLog("Admin", admin.getAdminId(), admin.getUsername(),
                    "RESOLVE_ALERT", "Resolved alert ID: " + alertId));
        }
        return ok;
    }

    public void printAlert(Alert a) {
        String levelIcon = a.getAlertLevel().equals("Critical") ? "🔴" :
                           a.getAlertLevel().equals("High")     ? "🟠" :
                           a.getAlertLevel().equals("Medium")   ? "🟡" : "🟢";
        System.out.println("  ─────────────────────────────────────────────────");
        System.out.printf("  Alert ID  : %d  %s [%s]%n", a.getAlertId(), levelIcon, a.getAlertLevel());
        System.out.println("  Type      : " + a.getAlertType());
        System.out.println("  Content   : " + a.getContentType() +
                (a.getArticleId() > 0 ? " #" + a.getArticleId() : " #" + a.getPostId()));
        System.out.println("  Message   : " + a.getAlertMessage());
        System.out.println("  Status    : " + (a.isResolved() ? "Resolved" : "OPEN"));
        System.out.println("  Created   : " + a.getCreatedAt());
    }

    public void printAlertList(List<Alert> alerts, String header) {
        System.out.println("\n  " + header);
        if (alerts.isEmpty()) {
            System.out.println("  No alerts found.");
            return;
        }
        for (Alert a : alerts) printAlert(a);
        System.out.println("  ─────────────────────────────────────────────────");
        System.out.println("  Total: " + alerts.size() + " alert(s).");
    }
}
