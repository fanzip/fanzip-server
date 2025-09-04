package org.example.fanzip.influencer.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.fanzip.influencer.domain.enums.InfluencerCategory;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InfluencerProfileUpdateRequestDTO {
    private String influencerName; // 인플루언서명
    private String description; // 소개
    private InfluencerCategory category; // 카테고리
}