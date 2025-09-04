package org.example.fanzip.membership.domain;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

// 추후 삭제 예정(통합 후 삭제 예정)
@Getter
public class Membership {
    private final Long membershipId;
    private final Long userId;
    private final Long influencerId;
    private final int gradeId; // Silver:1, Gold:2, Platinum:3
    private final LocalDate subscriptionStart;
    private final LocalDate subscriptionEnd;
    private final double monthlyAmount;
    private final double totalPaidAmount;
    private final String status; // ACTIVE, CANCELLED, EXPIRED
    private final boolean autoRenewal;

    @Builder
    public Membership(Long membershipId, Long userId, Long influencerId, int gradeId,
                      LocalDate subscriptionStart, LocalDate subscriptionEnd,
                      double monthlyAmount, double totalPaidAmount, String status,
                      boolean autoRenewal) {
        this.membershipId = membershipId;
        this.userId = userId;
        this.influencerId = influencerId;
        this.gradeId = gradeId;
        this.subscriptionStart = subscriptionStart;
        this.subscriptionEnd = subscriptionEnd;
        this.monthlyAmount = monthlyAmount;
        this.totalPaidAmount = totalPaidAmount;
        this.status = status;
        this.autoRenewal = autoRenewal;
    }
}