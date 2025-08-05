package org.example.fanzip.influencer.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.fanzip.influencer.domain.InfluencerVO;
import org.example.fanzip.influencer.domain.enums.InfluencerCategory;

import java.util.List;

@Mapper
public interface InfluencerMapper {

    List<InfluencerVO> findAllFiltered(@Param("userId") Long userId,
                                       @Param("category") InfluencerCategory category);

    InfluencerVO findDetailed(@Param("influencerId") Long influencerId);

    // 프로필 조회
    InfluencerVO findProfile(@Param("influencerId") Long influencerId);

    // 프로필 수정
    int updateProfile(@Param("influencerId") Long influencerId,
                      @Param("influencerName") String influencerName,
                      @Param("description") String description,
                      @Param("category") InfluencerCategory category);

    // 프로필 이미지 수정
    int updateProfileImage(@Param("influencerId") Long influencerId,
                           @Param("profileImage") String profileImage);
}
