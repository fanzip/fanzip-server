package org.example.fanzip.fancard.dto.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MembershipGradeDto {
    private Long gradeId;
    private String gradeName;
    private String color;
    private String benefitsDescription;
}