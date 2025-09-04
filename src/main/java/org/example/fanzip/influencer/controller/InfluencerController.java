package org.example.fanzip.influencer.controller;

import io.swagger.annotations.*;
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
import org.example.fanzip.payment.dto.RevenueResponseDto;
import org.example.fanzip.payment.service.RevenueService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.example.fanzip.security.CustomUserPrincipal;

import java.util.List;

@Api(tags = "Influencer", description = "인플루언서 관리 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/influencers")
public class InfluencerController {

    private final InfluencerService influencerService;
    private final RevenueService revenueService;

    @ApiOperation(value = "인플루언서 목록 조회", notes = "카테고리별로 인플루언서 목록을 조회합니다. 사용자가 구독한 인플루언서는 제외됩니다.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "인플루언서 목록 조회 성공"),
            @ApiResponse(code = 401, message = "인증 필요"),
            @ApiResponse(code = 500, message = "서버 오류")
    })
    @GetMapping
    public ResponseEntity<List<InfluencerResponseDTO>> getInfluencers(
            @ApiParam(value = "인플루언서 카테고리 (BEAUTY, GAME, DAILY, FASHION, COOKING, HEALTH, PET, KIDS, EDUCATION, TRAVEL, MUSIC, FITNESS, SPORTS, LANGUAGE)", example = "BEAUTY")
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

    @ApiOperation(value = "인플루언서 상세 정보 조회", notes = "특정 인플루언서의 상세 정보를 조회합니다.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "인플루언서 상세 정보 조회 성공"),
            @ApiResponse(code = 401, message = "인증 필요"),
            @ApiResponse(code = 404, message = "인플루언서를 찾을 수 없음"),
            @ApiResponse(code = 500, message = "서버 오류")
    })
    @GetMapping("/{influencerId}")
    public ResponseEntity<InfluencerDetailResponseDTO> getDetail(
            @ApiParam(value = "인플루언서 ID", required = true, example = "1")
            @PathVariable Long influencerId,
            @AuthenticationPrincipal CustomUserPrincipal principal) {

        Long userId = principal.getUserId();

        InfluencerDetailResponseDTO responseDTO = influencerService.findDetailed(userId, influencerId);
        return ResponseEntity.ok(responseDTO);
    }

    @ApiOperation(value = "인플루언서 프로필 조회", notes = "지정된 인플루언서의 프로필 정보를 조회합니다.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "인플루언서 프로필 조회 성공"),
            @ApiResponse(code = 401, message = "인증 필요"),
            @ApiResponse(code = 403, message = "권한 없음"),
            @ApiResponse(code = 404, message = "인플루언서를 찾을 수 없음"),
            @ApiResponse(code = 500, message = "서버 오류")
    })
    @GetMapping("/{influencerId}/profile")
    public ResponseEntity<InfluencerProfileResponseDTO> getProfile(
            @ApiParam(value = "인플루언서 ID", required = true, example = "1")
            @PathVariable Long influencerId,
            @AuthenticationPrincipal CustomUserPrincipal principal) {

        Long userId = principal.getUserId();

        InfluencerProfileResponseDTO responseDTO = influencerService.findProfile(influencerId, userId);
        return ResponseEntity.ok(responseDTO);
    }

    @ApiOperation(value = "내 인플루언서 프로필 조회", notes = "현재 로그인된 인플루언서의 프로필 정보를 조회합니다.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "내 인플루언서 프로필 조회 성공"),
            @ApiResponse(code = 401, message = "인증 필요"),
            @ApiResponse(code = 403, message = "인플루언서 권한 없음"),
            @ApiResponse(code = 500, message = "서버 오류")
    })
    @GetMapping("/profile")
    public ResponseEntity<InfluencerProfileResponseDTO> getMyProfile(
            @AuthenticationPrincipal CustomUserPrincipal principal
    ){
        Long userId = principal.getUserId();

        InfluencerProfileResponseDTO responseDTO=influencerService.findMyProfile(userId);
        return ResponseEntity.ok(responseDTO);
    }

    @ApiOperation(value = "인플루언서 프로필 수정", notes = "인플루언서의 프로필 정보를 수정합니다.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "인플루언서 프로필 수정 성공"),
            @ApiResponse(code = 400, message = "잘못된 요청 데이터"),
            @ApiResponse(code = 401, message = "인증 필요"),
            @ApiResponse(code = 403, message = "권한 없음"),
            @ApiResponse(code = 404, message = "인플루언서를 찾을 수 없음"),
            @ApiResponse(code = 500, message = "서버 오류")
    })
    @PutMapping("/{influencerId}/profile")
    public ResponseEntity<Void> updateProfile(
            @ApiParam(value = "인플루언서 ID", required = true, example = "1")
            @PathVariable Long influencerId,
            @ApiParam(value = "프로필 수정 요청 데이터", required = true)
            @RequestBody InfluencerProfileUpdateRequestDTO requestDTO,
            @AuthenticationPrincipal CustomUserPrincipal principal) {

        Long userId = principal.getUserId();

        influencerService.updateInfluencerProfile(influencerId, requestDTO, userId);
        return ResponseEntity.ok().build();
    }


    @ApiOperation(value = "일별 구독자 통계 조회", notes = "인플루언서의 일별 구독자 통계 데이터를 조회합니다.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "일별 구독자 통계 조회 성공"),
            @ApiResponse(code = 404, message = "인플루언서를 찾을 수 없음"),
            @ApiResponse(code = 500, message = "서버 오류")
    })
    @GetMapping("/{influencerId}/subscribers/stats/daily")
    public ResponseEntity<List<SubscriberStatsResponseDTO>> getSubscriberStatsDaily(
            @ApiParam(value = "인플루언서 ID", required = true, example = "1")
            @PathVariable Long influencerId) {
        List<SubscriberStatsResponseDTO> stats = influencerService.getSubscriberStatsDaily(influencerId);
        return ResponseEntity.ok(stats);
    }

    @ApiOperation(value = "주별 구독자 통계 조회", notes = "인플루언서의 주별 구독자 통계 데이터를 조회합니다.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "주별 구독자 통계 조회 성공"),
            @ApiResponse(code = 404, message = "인플루언서를 찾을 수 없음"),
            @ApiResponse(code = 500, message = "서버 오류")
    })
    @GetMapping("/{influencerId}/subscribers/stats/weekly")
    public ResponseEntity<List<SubscriberStatsResponseDTO>> getSubscriberStatsWeekly(
            @ApiParam(value = "인플루언서 ID", required = true, example = "1")
            @PathVariable Long influencerId) {
        List<SubscriberStatsResponseDTO> responseDTO = influencerService.getSubscriberStatsWeekly(influencerId);
        return ResponseEntity.ok(responseDTO);
    }



    @ApiOperation(value = "월별 구독자 통계 조회", notes = "인플루언서의 월별 구독자 통계 데이터를 조회합니다.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "월별 구독자 통계 조회 성공"),
            @ApiResponse(code = 404, message = "인플루언서를 찾을 수 없음"),
            @ApiResponse(code = 500, message = "서버 오류")
    })
    @GetMapping("/{influencerId}/subscribers/stats/monthly")
    public ResponseEntity<List<SubscriberStatsResponseDTO>> getSubscriberStatsMonthly(
            @ApiParam(value = "인플루언서 ID", required = true, example = "1")
            @PathVariable Long influencerId) {
        List<SubscriberStatsResponseDTO> responseDTO = influencerService.getSubscriberStatsMonthly(influencerId);
        return ResponseEntity.ok(responseDTO);
    }



    @ApiOperation(value = "실시간 구독자 현황 조회", notes = "인플루언서의 실시간 구독자 현황과 오늘 증감 정보를 조회합니다.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "구독자 현황 조회 성공"),
            @ApiResponse(code = 404, message = "인플루언서를 찾을 수 없음"),
            @ApiResponse(code = 500, message = "서버 오류")
    })
    @GetMapping("/{influencerId}/subscribers/status")
    public ResponseEntity<SubscriberStatusResponseDTO> getSubscriberStatus(
            @ApiParam(value = "인플루언서 ID", required = true, example = "1")
            @PathVariable Long influencerId) {
        SubscriberStatusResponseDTO responseDTO = influencerService.getSubscriberStatus(influencerId);
        return ResponseEntity.ok(responseDTO);
    }

    @ApiOperation(value = "월별 수익 추이 조회", notes = "인플루언서의 월별 수익 추이 데이터를 조회합니다.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "월별 수익 추이 조회 성공"),
            @ApiResponse(code = 404, message = "인플루언서를 찾을 수 없음"),
            @ApiResponse(code = 500, message = "서버 오류")
    })
    @GetMapping("/{influencerId}/revenue/monthly")
    public ResponseEntity<List<RevenueResponseDto>> getMonthlyRevenue(
            @ApiParam(value = "인플루언서 ID", required = true, example = "1")
            @PathVariable Long influencerId) {
        List<RevenueResponseDto> revenue = revenueService.getMonthlyRevenue(influencerId);
        return ResponseEntity.ok(revenue);
    }

    @ApiOperation(value = "오늘 수익 조회", notes = "인플루언서의 오늘 수익 정보를 조회합니다.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "오늘 수익 조회 성공"),
            @ApiResponse(code = 404, message = "인플루언서를 찾을 수 없음"),
            @ApiResponse(code = 500, message = "서버 오류")
    })
    @GetMapping("/{influencerId}/revenue/today")
    public ResponseEntity<RevenueResponseDto> getTodayRevenue(
            @ApiParam(value = "인플루언서 ID", required = true, example = "1")
            @PathVariable Long influencerId) {
        RevenueResponseDto revenue = revenueService.getTodayRevenue(influencerId);
        return ResponseEntity.ok(revenue);
    }

    @ApiOperation(value = "누적 수익 조회", notes = "인플루언서의 첫 결제일부터 현재까지의 누적 수익 정보를 조회합니다.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "누적 수익 조회 성공"),
            @ApiResponse(code = 404, message = "인플루언서를 찾을 수 없음"),
            @ApiResponse(code = 500, message = "서버 오류")
    })
    @GetMapping("/{influencerId}/revenue/total")
    public ResponseEntity<RevenueResponseDto> getTotalRevenue(
            @ApiParam(value = "인플루언서 ID", required = true, example = "1")
            @PathVariable Long influencerId) {
        RevenueResponseDto revenue = revenueService.getTotalRevenue(influencerId);
        return ResponseEntity.ok(revenue);
    }
}
