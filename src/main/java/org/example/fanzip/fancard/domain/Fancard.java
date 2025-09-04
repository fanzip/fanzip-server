package org.example.fanzip.fancard.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Fancard {
    
    private Long cardId;
    private Long membershipId;
    private String cardNumber;
    private String cardDesignUrl;
    private Boolean isActive;
    private LocalDateTime createdAt;
    
    protected Fancard() {}
    
    public Fancard(Long membershipId, String cardNumber, String cardDesignUrl) {
        this.membershipId = membershipId;
        this.cardNumber = cardNumber;
        this.cardDesignUrl = cardDesignUrl;
        this.isActive = true;
        this.createdAt = LocalDateTime.now();
    }
    
    public Long getCardId() { return cardId; }
    public Long getMembershipId() { return membershipId; }
    public String getCardNumber() { return cardNumber; }
    public String getCardDesignUrl() { return cardDesignUrl; }
    public Boolean getIsActive() { return isActive; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    
    public void deactivate() {
        this.isActive = false;
    }
    
    public void activate() {
        this.isActive = true;
    }
}
