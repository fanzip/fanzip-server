package org.example.fanzip.membership.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.fanzip.membership.domain.enums.MembershipStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MembershipVO {

    private Long membershipId;
    private Long userId;
    private Long influencerId;
    private Integer gradeId;
    private MembershipStatus status;
    private LocalDate subscriptionStart;
    private LocalDate subscriptionEnd;

    private BigDecimal monthlyAmount;
    private BigDecimal totalPaidAmount;
    private Boolean autoRenewal;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
