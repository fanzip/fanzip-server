package org.example.fanzip.influencer.service;

import lombok.RequiredArgsConstructor;
import org.example.fanzip.influencer.domain.InfluencerVO;
import org.example.fanzip.influencer.domain.enums.InfluencerCategory;
import org.example.fanzip.influencer.dto.InfluencerRequestDTO;
import org.example.fanzip.influencer.dto.InfluencerResponseDTO;
import org.example.fanzip.influencer.mapper.InfluencerMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InfluencerServiceImpl implements InfluencerService {

    private final InfluencerMapper influencerMapper;

    @Override
    public List<InfluencerResponseDTO> findAll(InfluencerRequestDTO requestDTO) {

        // 1. 파라미터 구성
        // userId와 category를 Map으로 묶어 보냄
        Long userId = requestDTO.getUserId();
        InfluencerCategory category = requestDTO.getCategory();

        List<InfluencerVO> influencerList = influencerMapper.findAllFiltered(userId, category);


        // 2. VO → ResponseDTO로 변환
        // 각 InfluencerVO 객체를 InfluencerResponseDTO로 변환
        // 최종적으로 클라이언트에게 반환될 형태의 리스트를 리턴
        return influencerList.stream()
                .map(InfluencerResponseDTO::from)
                .collect(Collectors.toList());
    }
}
