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
public class MembershipSubscribeRequestDTO {
    private long influencerId;
    private int gradeId;
    private boolean autoRenewal;
    private BigDecimal monthlyAmount;

    public MembershipVO toEntity(long userId) {
        LocalDate now = LocalDate.now();

        return MembershipVO.builder()
                .userId(userId)
                .influencerId(this.influencerId)
                .gradeId(this.gradeId)
                .status(MembershipStatus.ACTIVE)
                .subscriptionStart(now)
                .subscriptionEnd(now.plusMonths(1))
                .monthlyAmount(this.monthlyAmount)
                .totalPaidAmount(this.monthlyAmount)
                .autoRenewal(this.autoRenewal)
                .build();
    }

}
