package org.example.fanzip.influencer.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.fanzip.influencer.domain.InfluencerVO;
import org.example.fanzip.membership.dto.MembershipGradeDTO;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InfluencerDetailResponseDTO {

    private Long influencerId;
    private String profileImage;
    private String influencerName;
    private String description;
    private List<MembershipGradeDTO> membershipGrades;


    public static InfluencerDetailResponseDTO from(InfluencerVO influencerVO, List<MembershipGradeDTO> grades){
        return InfluencerDetailResponseDTO.builder()
                .influencerId(influencerVO.getInfluencerId())
                .profileImage(influencerVO.getProfileImage())
                .influencerName(influencerVO.getInfluencerName())
                .description(influencerVO.getDescription())
                .membershipGrades(grades)
                .build();
    }
}
