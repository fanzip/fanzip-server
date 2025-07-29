package org.example.fanzip.fancard.dto.response;

import lombok.Builder;

@Builder
public record BenefitDto(
    Long benefitId,
    String benefitType,
    String benefitName,
    String benefitValue,
    String description,
    Boolean isActive
) {}