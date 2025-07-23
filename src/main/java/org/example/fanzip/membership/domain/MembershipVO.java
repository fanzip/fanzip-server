package org.example.fanzip.membership.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.fanzip.membership.domain.enums.MembershipStatus;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MembershipVO {

    public Long membershipId;
    public Long userId;
    public Long influencerId;
    public Integer gradeId;
    public MembershipStatus status;
    public Date subscriptionStart;
    public Date subscriptionEnd;

    private Double monthlyAmount;
    private Double totalPaidAmount;
    private Boolean autoRenewal;
    private Date createdAt;
    private Date updatedAt;
}
