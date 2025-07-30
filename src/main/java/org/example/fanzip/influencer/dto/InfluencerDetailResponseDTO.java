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
public class InfluencerDetailResponseDTO {

    private Long influencerId;
    private String influencerName;
    private String coverImage;
    private String description;


    public static InfluencerDetailResponseDTO from(InfluencerVO influencerVO){
        return InfluencerDetailResponseDTO.builder()
                .influencerId(influencerVO.getInfluencerId())
                .influencerName(influencerVO.getInfluencerName())
                .coverImage(influencerVO.getCoverImage())
                .description(influencerVO.getDescription())
                .build();
    }
}
