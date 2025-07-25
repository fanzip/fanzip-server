package org.example.fanzip.membership.dto;


import lombok.*;
import org.example.fanzip.membership.domain.MembershipVO;
import org.example.fanzip.membership.domain.enums.MembershipStatus;

// 수정
import java.time.LocalDate;


@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MembershipSubscribeResponseDTO {
    private long membershipId;
    private MembershipStatus status;
    private LocalDate subscriptionStart;
    private LocalDate subscriptionEnd;


    public static MembershipSubscribeResponseDTO from(MembershipVO membershipVO){
        return MembershipSubscribeResponseDTO.builder()
                .membershipId(membershipVO.getMembershipId())
                .status(membershipVO.getStatus())
                .subscriptionStart(membershipVO.getSubscriptionStart())
                .subscriptionEnd(membershipVO.getSubscriptionEnd())
                .build();
    }
}
