package org.example.fanzip.fancard.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BenefitDto {
    private Long benefitId;
    private String benefitType;
    private String benefitName;
    private String benefitValue;
    private String description;
    private Boolean isActive;
}