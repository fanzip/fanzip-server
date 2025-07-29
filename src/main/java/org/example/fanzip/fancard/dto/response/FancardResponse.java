package org.example.fanzip.fancard.dto.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor; 
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Value
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED, force = true)
public class FancardResponse {
    Long cardId;
    Long membershipId;
    String cardNumber;
    String qrCode;
    LocalDate issueDate;
    LocalDate expiryDate;
    String cardDesignUrl;
    Boolean isActive;
    String influencerName;
    String gradeName;
    String gradeColor;
    LocalDateTime createdAt;
}
