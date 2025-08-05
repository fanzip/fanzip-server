package org.example.fanzip.influencer.service;

import lombok.RequiredArgsConstructor;
import org.example.fanzip.fancard.mapper.FancardMapper;
import org.example.fanzip.influencer.domain.InfluencerVO;
import org.example.fanzip.influencer.domain.enums.InfluencerCategory;
import org.example.fanzip.influencer.dto.*;
import org.example.fanzip.influencer.mapper.InfluencerMapper;
import org.springframework.stereotype.Service;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InfluencerServiceImpl implements InfluencerService {

    private final InfluencerMapper influencerMapper;
    private final FancardMapper fancardMapper;

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

    // 인플루언서 관리자 마이페이지 프로필 조회
    @Override
    @Transactional(readOnly = true)
    public InfluencerProfileResponseDTO findProfile(Long influencerId, Long userId) {
        InfluencerVO influencer = influencerMapper.findDetailed(influencerId);
        
        if (influencer == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "해당 인플루언서를 찾을 수 없습니다.");
        }
        
        // isVerified는 임시로 true로 설정 (실제로는 DB에서 조회하거나 비즈니스 로직에 따라 결정)
        Boolean isVerified = true;
        
        return InfluencerProfileResponseDTO.from(influencer, userId, isVerified);
    }

    // 인플루언서 프로필 수정 (공개 API용)
    @Override
    @Transactional
    public void updateInfluencerProfile(Long influencerId, InfluencerProfileUpdateRequestDTO requestDTO, Long userId) {
        // 인플루언서 존재 여부 확인
        InfluencerVO influencer = influencerMapper.findDetailed(influencerId);
        if (influencer == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "해당 인플루언서를 찾을 수 없습니다.");
        }

        // 권한 검증 로직 (필요시 추가)
        // 예: 해당 인플루언서의 소유자인지 확인

        int updated = influencerMapper.updateProfile(
                influencerId,
                requestDTO.getInfluencerName(),
                requestDTO.getDescription(),
                requestDTO.getCategory()
        );
        
        if (updated == 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "프로필 수정에 실패했습니다.");
        }
    }

    // 인플루언서 프로필 이미지 업로드 (공개 API용)
    @Override
    @Transactional
    public void updateInfluencerProfileImage(Long influencerId, InfluencerProfileImgUpdateRequestDTO requestDTO, Long userId) {
        // 인플루언서 존재 여부 확인
        InfluencerVO influencer = influencerMapper.findDetailed(influencerId);
        if (influencer == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "해당 인플루언서를 찾을 수 없습니다.");
        }

        // 권한 검증 로직 (필요시 추가)
        // 예: 해당 인플루언서의 소유자인지 확인

        int updated = influencerMapper.updateProfileImage(influencerId, requestDTO.getProfileImage());
        if (updated == 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "프로필 이미지 업로드에 실패했습니다.");
        }
    }

    // 인플루언서 팬카드 이미지 업로드 (해당 인플루언서의 모든 활성 팬카드 디자인 업데이트)
    @Override
    @Transactional
    public void updateInfluencerFanCardImage(Long influencerId, InfluencerFanCardImageUpdateRequestDTO requestDTO, Long userId) {
        // 인플루언서 존재 여부 확인
        InfluencerVO influencer = influencerMapper.findDetailed(influencerId);
        if (influencer == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "해당 인플루언서를 찾을 수 없습니다.");
        }

        // 권한 검증 로직 (필요시 추가)
        // 예: 해당 인플루언서의 소유자인지 확인

        // 해당 인플루언서의 모든 활성 팬카드 디자인 업데이트
        int updated = fancardMapper.updateCardDesignByInfluencerId(influencerId, requestDTO.getFanCardImage());
        
        if (updated == 0) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "업데이트할 활성 팬카드가 없습니다.");
        }
    }

}
