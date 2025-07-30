package org.example.fanzip.influencer.service;

import lombok.RequiredArgsConstructor;
import org.example.fanzip.influencer.domain.InfluencerVO;
import org.example.fanzip.influencer.domain.enums.InfluencerCategory;
import org.example.fanzip.influencer.dto.InfluencerDetailResponseDTO;
import org.example.fanzip.influencer.dto.InfluencerRequestDTO;
import org.example.fanzip.influencer.dto.InfluencerResponseDTO;
import org.example.fanzip.influencer.mapper.InfluencerMapper;
import org.springframework.stereotype.Service;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InfluencerServiceImpl implements InfluencerService {

    private final InfluencerMapper influencerMapper;

    // 전체 목록 조회
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

    // 상세 조회
    @Override
    public InfluencerDetailResponseDTO findDetailed(Long userId, Long influencerId) {

        // 인플루언서Id로 DB 조회하고 DTO로 변환해서 클라로 보내기
        InfluencerVO influencerDetail = influencerMapper.findDetailed(influencerId);

        // 예외
        if (influencerDetail == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "해당 인플루언서를 찾을 수 없습니다.");
        }

        return InfluencerDetailResponseDTO.from(influencerDetail);
    }
}
