package org.example.fanzip.membership.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MembershipGradeDTO {
    private Long gradeId;
    private String gradeName;              // 등급명
    private String benefitsDescription;    // 혜택 설명(문장)
    private String color;                  // 배경색 HEX (#000000)
    private BigDecimal monthlyAmount;      // 금액
}
