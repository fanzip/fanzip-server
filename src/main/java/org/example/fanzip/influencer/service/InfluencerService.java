package org.example.fanzip.influencer.service;


import org.example.fanzip.influencer.dto.InfluencerDetailResponseDTO;
import org.example.fanzip.influencer.dto.InfluencerRequestDTO;
import org.example.fanzip.influencer.dto.InfluencerResponseDTO;

import java.util.List;

public interface InfluencerService {

    List<InfluencerResponseDTO> findAll(InfluencerRequestDTO requestDTO);

    InfluencerDetailResponseDTO findDetailed(Long userId, Long influencerId);
}
