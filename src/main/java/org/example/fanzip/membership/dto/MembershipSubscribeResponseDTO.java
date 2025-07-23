package org.example.fanzip.membership.dto;


import lombok.*;
import org.example.fanzip.membership.domain.MembershipVO;
import org.example.fanzip.membership.domain.enums.MembershipStatus;

import java.util.Date;


@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MembershipSubscribeResponseDTO {
    private long membershipId;
    private MembershipStatus status;
    private Date subscriptionStart;
    private Date subscriptionEnd;


    public static MembershipSubscribeResponseDTO from(MembershipVO membershipVO){
        return MembershipSubscribeResponseDTO.builder()
                .membershipId(membershipVO.getMembershipId())
                .status(membershipVO.getStatus())
                .subscriptionStart(membershipVO.getSubscriptionStart())
                .subscriptionEnd(membershipVO.getSubscriptionEnd())
                .build();
    }
}
