package org.example.fanzip.fancard.dto.response;

import lombok.Builder;

@Builder
public record MembershipGradeDto(
    Long gradeId,
    String gradeName,
    String color,
    String benefitsDescription
) {}