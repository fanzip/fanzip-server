package org.example.fanzip.influencer.service;


import org.example.fanzip.influencer.dto.InfluencerDetailResponseDTO;
import org.example.fanzip.influencer.dto.InfluencerProfileResponseDTO;
import org.example.fanzip.influencer.dto.InfluencerProfileUpdateRequestDTO;
import org.example.fanzip.influencer.dto.InfluencerRequestDTO;
import org.example.fanzip.influencer.dto.InfluencerResponseDTO;
import org.example.fanzip.influencer.dto.SubscriberStatsResponseDTO;
import org.example.fanzip.influencer.dto.SubscriberStatusResponseDTO;
import org.example.fanzip.influencer.dto.SubscriberTrendResponseDTO;

import java.util.List;

public interface InfluencerService {

    List<InfluencerResponseDTO> findAll(InfluencerRequestDTO requestDTO);

    InfluencerDetailResponseDTO findDetailed(Long userId, Long influencerId);

    InfluencerProfileResponseDTO findProfile(Long influencerId, Long userId);

    void updateInfluencerProfile(Long influencerId, InfluencerProfileUpdateRequestDTO requestDTO, Long userId);


    SubscriberStatsResponseDTO getSubscriberStatsDaily(Long influencerId);

    List<SubscriberStatsResponseDTO> getSubscriberStatsWeekly(Long influencerId);

    SubscriberStatsResponseDTO getSubscriberStatsMonthly(Long influencerId);

    List<SubscriberTrendResponseDTO> getSubscriberTrendsWeekly(Long influencerId);

    SubscriberStatusResponseDTO getSubscriberStatus(Long influencerId);
}
