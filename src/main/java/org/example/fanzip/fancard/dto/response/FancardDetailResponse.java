package org.example.fanzip.fancard.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FancardDetailResponse {
    private Long cardId;
    private String cardNumber;
    private String cardDesignUrl;
    private InfluencerDto influencer;
    private MembershipDto membership;
    private List<BenefitDto> benefits;
}