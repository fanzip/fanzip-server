package org.example.fanzip.influencer.controller;


import lombok.RequiredArgsConstructor;
import org.example.fanzip.influencer.domain.enums.InfluencerCategory;
import org.example.fanzip.influencer.dto.InfluencerDetailResponseDTO;
import org.example.fanzip.influencer.dto.InfluencerProfileResponseDTO;
import org.example.fanzip.influencer.dto.InfluencerProfileUpdateRequestDTO;
import org.example.fanzip.influencer.dto.InfluencerRequestDTO;
import org.example.fanzip.influencer.dto.InfluencerResponseDTO;
import org.example.fanzip.influencer.dto.SubscriberStatsResponseDTO;
import org.example.fanzip.influencer.dto.SubscriberStatusResponseDTO;
import org.example.fanzip.influencer.service.InfluencerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.example.fanzip.security.CustomUserPrincipal;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/influencers")
public class InfluencerController {

    private final InfluencerService influencerService;

    // 전체 인플루언서 목록 조회 (필터: 선택적 카테고리 지정 가능)
    @GetMapping
    public ResponseEntity<List<InfluencerResponseDTO>> getInfluencers(
            // 카테고리 지정하면 /api/influencers/?category= 형태로 조회
            @RequestParam(value = "category", required = false) InfluencerCategory category,
            @AuthenticationPrincipal CustomUserPrincipal principal) {

        // Security Context에서 userId를 가져옴 (구독한 인플루언서 제외용)
        Long userId = principal.getUserId();
        
        InfluencerRequestDTO requestDTO = InfluencerRequestDTO.builder()
                .userId(userId)
                .category(category)
                .build();
        
        List<InfluencerResponseDTO> influencerList = influencerService.findAll(requestDTO);

        return ResponseEntity.ok(influencerList);

    }

    // 인플루언서 상세 조회
    @GetMapping("/{influencerId}")
    public ResponseEntity<InfluencerDetailResponseDTO> getDetail(
            @PathVariable Long influencerId,
            @AuthenticationPrincipal CustomUserPrincipal principal) {

        Long userId = principal.getUserId();

        InfluencerDetailResponseDTO responseDTO = influencerService.findDetailed(userId, influencerId);
        return ResponseEntity.ok(responseDTO);
    }

    // 인플루언서 관리자 마이페이지 프로필 조회
    @GetMapping("/{influencerId}/profile")
    public ResponseEntity<InfluencerProfileResponseDTO> getProfile(
            @PathVariable Long influencerId,
            @AuthenticationPrincipal CustomUserPrincipal principal) {

        Long userId = principal.getUserId();

        InfluencerProfileResponseDTO responseDTO = influencerService.findProfile(influencerId, userId);
        return ResponseEntity.ok(responseDTO);
    }

    // 인플루언서 프로필 수정
    @PutMapping("/{influencerId}/profile")
    public ResponseEntity<Void> updateProfile(
            @PathVariable Long influencerId,
            @RequestBody InfluencerProfileUpdateRequestDTO requestDTO,
            @AuthenticationPrincipal CustomUserPrincipal principal) {

        Long userId = principal.getUserId();

        influencerService.updateInfluencerProfile(influencerId, requestDTO, userId);
        return ResponseEntity.ok().build();
    }


    // 일별 구독자 통계
    @GetMapping("/{influencerId}/subscribers/stats/daily")
    public ResponseEntity<SubscriberStatsResponseDTO> getSubscriberStatsDaily(@PathVariable Long influencerId) {
        SubscriberStatsResponseDTO responseDTO = influencerService.getSubscriberStatsDaily(influencerId);
        return ResponseEntity.ok(responseDTO);
    }

    // 주별 구독자 통계
    @GetMapping("/{influencerId}/subscribers/stats/weekly")
    public ResponseEntity<List<SubscriberStatsResponseDTO>> getSubscriberStatsWeekly(@PathVariable Long influencerId) {
        List<SubscriberStatsResponseDTO> responseDTO = influencerService.getSubscriberStatsWeekly(influencerId);
        return ResponseEntity.ok(responseDTO);
    }


    // 월별 구독자 통계
    @GetMapping("/{influencerId}/subscribers/stats/monthly")
    public ResponseEntity<SubscriberStatsResponseDTO> getSubscriberStatsMonthly(@PathVariable Long influencerId) {
        SubscriberStatsResponseDTO responseDTO = influencerService.getSubscriberStatsMonthly(influencerId);
        return ResponseEntity.ok(responseDTO);
    }


    // 실시간 구독자 현황 (오늘 증감 포함)
    @GetMapping("/{influencerId}/subscribers/status")
    public ResponseEntity<SubscriberStatusResponseDTO> getSubscriberStatus(@PathVariable Long influencerId) {
        SubscriberStatusResponseDTO responseDTO = influencerService.getSubscriberStatus(influencerId);
        return ResponseEntity.ok(responseDTO);
    }
}
