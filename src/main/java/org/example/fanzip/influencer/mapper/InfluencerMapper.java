package org.example.fanzip.influencer.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.fanzip.influencer.domain.InfluencerVO;
import org.example.fanzip.influencer.domain.enums.InfluencerCategory;
import org.example.fanzip.influencer.dto.SubscriberStatsResponseDTO;
import org.example.fanzip.influencer.dto.SubscriberStatusResponseDTO;

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

    // 프로필 이미지 조회
    String selectProfileImageUrl(@Param("influencerId") Long influencerId);

    // 프로필 이미지 수정
    int updateProfileImage(@Param("influencerId") Long influencerId,
                           @Param("profileImage") String profileImage);

    // 팬카드 이미지 수정
    int updateFanCardImageUrl(@Param("influencerId") Long influencerId,
                              @Param("fanCardImage") String fanCardImage);

    // 팬카드 이미지 조회
    String selectFanCardImageUrl(@Param("influencerId") Long influencerId);


    // 일별 구독자 통계
    SubscriberStatsResponseDTO getSubscriberStatsDaily(@Param("influencerId") Long influencerId);

    // 주별 구독자 통계
    List<SubscriberStatsResponseDTO> getSubscriberStatsWeekly(@Param("influencerId") Long influencerId);

    // 월별 구독자 통계
    SubscriberStatsResponseDTO getSubscriberStatsMonthly(@Param("influencerId") Long influencerId);


    // 실시간 구독자 현황
    SubscriberStatusResponseDTO getSubscriberStatus(@Param("influencerId") Long influencerId);

}
