package org.example.fanzip.influencer.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.fanzip.influencer.domain.enums.InfluencerCategory;

import java.sql.Timestamp;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InfluencerVO {
    private Long influencerId;        // PK
    private String influencerName;    // 인플루언서 이름
    private InfluencerCategory category; // 인플루언서 카테고리
    private String profileImage;      // 프로필 이미지 URL
}
