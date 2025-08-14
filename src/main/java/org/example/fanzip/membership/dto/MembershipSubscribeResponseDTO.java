package org.example.fanzip.membership.dto;

import lombok.*;
import org.example.fanzip.membership.domain.MembershipVO;
import org.example.fanzip.membership.domain.enums.MembershipStatus;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MembershipSubscribeResponseDTO {
    private long membershipId;
    private long influencerId;
    private int gradeId;
    private MembershipStatus status;
    private BigDecimal monthlyAmount;
    private long paymentId;
    private String paymentPageUrl;
    private LocalDate subscriptionStart;
    private LocalDate subscriptionEnd;

    public static MembershipSubscribeResponseDTO from(MembershipVO membershipVO){
        return MembershipSubscribeResponseDTO.builder()
                .membershipId(membershipVO.getMembershipId())
                .influencerId(membershipVO.getInfluencerId())
                .gradeId(membershipVO.getGradeId())
                .status(membershipVO.getStatus())
                .monthlyAmount(membershipVO.getMonthlyAmount())
                .subscriptionStart(membershipVO.getSubscriptionStart())
                .subscriptionEnd(membershipVO.getSubscriptionEnd())
                .build();
    }
}
