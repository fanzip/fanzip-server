package org.example.fanzip.fancard.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FancardListResponse {
    private Long cardId;
    private String cardNumber;
    private Long influencerId;
    private String influencerName;
    private String category;
    private MembershipGradeDto membershipGrade;
    private String cardDesignUrl;
    private Boolean isActive;
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate issueDate;
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate expiryDate;
}