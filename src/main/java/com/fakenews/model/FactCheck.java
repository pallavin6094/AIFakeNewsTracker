package com.fakenews.model;

import java.time.LocalDateTime;

/**
 * FactCheck Model - Stores fact-check verdicts for articles and posts.
 */
public class FactCheck {
    private int factCheckId;
    private String contentType;
    private int articleId;
    private int postId;
    private String claim;
    private String verdict;       // True / Mostly True / Partially True / Misleading / False
    private String explanation;
    private String referenceUrls;
    private int checkedBy;
    private LocalDateTime checkedAt;

    public FactCheck() {}

    public int getFactCheckId()                        { return factCheckId; }
    public void setFactCheckId(int factCheckId)        { this.factCheckId = factCheckId; }
    public String getContentType()                     { return contentType; }
    public void setContentType(String contentType)     { this.contentType = contentType; }
    public int getArticleId()                          { return articleId; }
    public void setArticleId(int articleId)            { this.articleId = articleId; }
    public int getPostId()                             { return postId; }
    public void setPostId(int postId)                  { this.postId = postId; }
    public String getClaim()                           { return claim; }
    public void setClaim(String claim)                 { this.claim = claim; }
    public String getVerdict()                         { return verdict; }
    public void setVerdict(String verdict)             { this.verdict = verdict; }
    public String getExplanation()                     { return explanation; }
    public void setExplanation(String explanation)     { this.explanation = explanation; }
    public String getReferenceUrls()                   { return referenceUrls; }
    public void setReferenceUrls(String urls)          { this.referenceUrls = urls; }
    public int getCheckedBy()                          { return checkedBy; }
    public void setCheckedBy(int checkedBy)            { this.checkedBy = checkedBy; }
    public LocalDateTime getCheckedAt()                { return checkedAt; }
    public void setCheckedAt(LocalDateTime t)          { this.checkedAt = t; }

    @Override
    public String toString() {
        return String.format("FactCheck[ID=%d, Type=%s, Verdict=%s]", factCheckId, contentType, verdict);
    }
}
