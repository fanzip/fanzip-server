package org.example.fanzip.influencer.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.fanzip.influencer.domain.InfluencerVO;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InfluencerProfileResponseDTO {

    private Long influencerId;
    private Long userId;
    private String influencerName;
    private String category;
    private String description;
    private String profileImage;
    private Boolean isVerified;

    public static InfluencerProfileResponseDTO from(InfluencerVO influencerVO, Long userId, Boolean isVerified) {
        return InfluencerProfileResponseDTO.builder()
                .influencerId(influencerVO.getInfluencerId())
                .userId(userId)
                .influencerName(influencerVO.getInfluencerName())
                .category(influencerVO.getCategory() != null ? influencerVO.getCategory().name() : null)
                .description(influencerVO.getDescription())
                .profileImage(influencerVO.getProfileImage())
                .isVerified(isVerified)
                .build();
    }
}