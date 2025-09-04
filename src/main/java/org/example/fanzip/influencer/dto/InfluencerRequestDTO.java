package org.example.fanzip.influencer.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.fanzip.influencer.domain.enums.InfluencerCategory;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InfluencerRequestDTO {
    private InfluencerCategory category;  // 필터링 조건(카테고리별 필터링)
    private Long userId;                  // 현재 로그인한 사용자가 구독한 인플루언서 목록을 제외하기 위해 사용
}
