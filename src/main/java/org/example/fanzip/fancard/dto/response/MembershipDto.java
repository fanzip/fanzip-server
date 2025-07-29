package org.example.fanzip.fancard.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MembershipDto {
    private Long membershipId;
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate subscriptionStart;
    
    private BigDecimal monthlyAmount;
    private BigDecimal totalPaidAmount;
    private String status;
    private Boolean autoRenewal;
    private MembershipGradeDto grade;
}