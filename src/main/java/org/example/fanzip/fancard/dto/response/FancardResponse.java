package org.example.fanzip.fancard.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class FancardResponse {
    private Long cardId;
    private Long membershipId;
    private String cardNumber;
    private String qrCode;
    private LocalDate issueDate;
    private LocalDate expiryDate;
    private String cardDesignUrl;
    private Boolean isActive;
    private String influencerName;
    private String gradeName;
    private String gradeColor;
    private LocalDateTime createdAt;
    
    public FancardResponse() {}
    
    public FancardResponse(Long cardId, Long membershipId, String cardNumber, String qrCode,
                          LocalDate issueDate, LocalDate expiryDate, String cardDesignUrl,
                          Boolean isActive, String influencerName, String gradeName, 
                          String gradeColor, LocalDateTime createdAt) {
        this.cardId = cardId;
        this.membershipId = membershipId;
        this.cardNumber = cardNumber;
        this.qrCode = qrCode;
        this.issueDate = issueDate;
        this.expiryDate = expiryDate;
        this.cardDesignUrl = cardDesignUrl;
        this.isActive = isActive;
        this.influencerName = influencerName;
        this.gradeName = gradeName;
        this.gradeColor = gradeColor;
        this.createdAt = createdAt;
    }
    
    public Long getCardId() { return cardId; }
    public Long getMembershipId() { return membershipId; }
    public String getCardNumber() { return cardNumber; }
    public String getQrCode() { return qrCode; }
    public LocalDate getIssueDate() { return issueDate; }
    public LocalDate getExpiryDate() { return expiryDate; }
    public String getCardDesignUrl() { return cardDesignUrl; }
    public Boolean getIsActive() { return isActive; }
    public String getInfluencerName() { return influencerName; }
    public String getGradeName() { return gradeName; }
    public String getGradeColor() { return gradeColor; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    
    public void setCardDesignUrl(String cardDesignUrl) { this.cardDesignUrl = cardDesignUrl; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
    public void setInfluencerName(String influencerName) { this.influencerName = influencerName; }
    public void setGradeName(String gradeName) { this.gradeName = gradeName; }
    public void setGradeColor(String gradeColor) { this.gradeColor = gradeColor; }
}
