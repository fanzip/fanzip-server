package org.example.fanzip.fancard.dto.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FancardDetailResponse {
    private Long cardId;
    private String cardNumber;
    private String cardDesignUrl;
    private Boolean isActive;
    private InfluencerDto influencer;
    private MembershipDto membership;
    private List<BenefitDto> benefits;
    private List<PaymentHistoryDto> paymentHistory;
}