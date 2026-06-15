package com.fakenews.service;

import com.fakenews.dao.FactCheckDAO;
import com.fakenews.dao.AuditLogDAO;
import com.fakenews.model.AuditLog;
import com.fakenews.model.FactCheck;
import com.fakenews.model.User;

import java.util.List;

/**
 * FactCheckService - Business logic for fact-checking operations.
 */
public class FactCheckService {

    private final FactCheckDAO factCheckDAO = new FactCheckDAO();
    private final AuditLogDAO  auditLogDAO  = new AuditLogDAO();

    public boolean addFactCheck(FactCheck fc, User user) {
        if (fc.getClaim() == null || fc.getClaim().trim().isEmpty()) {
            System.out.println("  [!] Claim cannot be empty.");
            return false;
        }
        if (fc.getVerdict() == null || fc.getVerdict().trim().isEmpty()) {
            System.out.println("  [!] Verdict cannot be empty.");
            return false;
        }
        fc.setCheckedBy(user.getUserId());
        int id = factCheckDAO.addFactCheck(fc);
        if (id > 0) {
            auditLogDAO.addLog(new AuditLog("User", user.getUserId(), user.getUsername(),
                    "FACT_CHECK", "Added fact check ID: " + id));
            return true;
        }
        return false;
    }

    public List<FactCheck> getFactChecksForArticle(int articleId) {
        return factCheckDAO.getFactChecksByArticle(articleId);
    }

    public List<FactCheck> getFactChecksForPost(int postId) {
        return factCheckDAO.getFactChecksByPost(postId);
    }

    public List<FactCheck> getAllFactChecks() {
        return factCheckDAO.getAllFactChecks();
    }

    public void printFactCheck(FactCheck fc) {
        System.out.println("  ─────────────────────────────────────────────────");
        System.out.println("  Fact Check ID : " + fc.getFactCheckId());
        System.out.println("  Content Type  : " + fc.getContentType());
        System.out.println("  Claim         : " + fc.getClaim());
        System.out.println("  Verdict       : " + fc.getVerdict());
        System.out.println("  Explanation   : " + fc.getExplanation());
        if (fc.getReferenceUrls() != null)
            System.out.println("  References    : " + fc.getReferenceUrls());
        System.out.println("  Checked At    : " + fc.getCheckedAt());
    }
}
