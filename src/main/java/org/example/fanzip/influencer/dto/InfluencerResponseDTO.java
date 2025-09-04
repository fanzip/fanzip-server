package org.example.fanzip.influencer.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.fanzip.influencer.domain.InfluencerVO;
import org.example.fanzip.influencer.domain.enums.InfluencerCategory;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InfluencerResponseDTO {

    private Long influencerId;
    private String influencerName;
    private String profileImage;
    private InfluencerCategory category;

    public static InfluencerResponseDTO from(InfluencerVO influencerVO){
        return InfluencerResponseDTO.builder()
                .influencerId(influencerVO.getInfluencerId())
                .influencerName(influencerVO.getInfluencerName())
                .profileImage(influencerVO.getProfileImage())
                .category(influencerVO.getCategory())
                .build();
    }

}
