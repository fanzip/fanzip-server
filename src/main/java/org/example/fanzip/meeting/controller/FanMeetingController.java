package org.example.fanzip.meeting.controller;

import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import org.example.fanzip.meeting.dto.FanMeetingDetailResponseDTO;
import org.example.fanzip.meeting.dto.FanMeetingRequestDTO;
import org.example.fanzip.meeting.dto.FanMeetingResponseDTO;
import org.example.fanzip.meeting.service.FanMeetingService;
import org.example.fanzip.security.CustomUserPrincipal;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "Fan Meeting", description = "팬미팅 관리 API")
@RestController
@RequestMapping("/api/fan-meetings")
@RequiredArgsConstructor
public class FanMeetingController {

    private final FanMeetingService fanMeetingService;

    @ApiOperation(value = "팬미팅 목록 조회", notes = "현재 개설된 팬미팅 목록을 등급별로 조회합니다.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "팬미팅 목록 조회 성공"),
            @ApiResponse(code = 500, message = "서버 오류")
    })
    @GetMapping
    public List<FanMeetingResponseDTO> getFanMeetings(
            @ApiParam(value = "사용자 등급 (GENERAL, WHITE, SILVER, GOLD, VIP)", example = "GENERAL")
            @RequestParam(required = false) String grade) {
        return fanMeetingService.getOpenMeetings(grade != null ? grade : "GENERAL");
    }

    @ApiOperation(value = "팬미팅 상세 정보 조회", notes = "특정 팬미팅의 상세 정보를 조회합니다.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "팬미팅 상세 정보 조회 성공"),
            @ApiResponse(code = 404, message = "팬미팅을 찾을 수 없음"),
            @ApiResponse(code = 500, message = "서버 오류")
    })
    @GetMapping("/{meetingId}")
    public FanMeetingDetailResponseDTO getFanMeetingDetail(
            @ApiParam(value = "팬미팅 ID", required = true, example = "1")
            @PathVariable Long meetingId) {
        return fanMeetingService.getMeetingDetail(meetingId);
    }

    @ApiOperation(value = "팬미팅 생성", notes = "새로운 팬미팅을 생성합니다.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "팬미팅 생성 성공"),
            @ApiResponse(code = 400, message = "잘못된 요청 데이터"),
            @ApiResponse(code = 401, message = "인증 필요"),
            @ApiResponse(code = 403, message = "권한 없음"),
            @ApiResponse(code = 500, message = "서버 오류")
    })
    @PostMapping
    public FanMeetingDetailResponseDTO createFanMeeting(
            @ApiParam(value = "팬미팅 생성 요청 데이터", required = true)
            @RequestBody FanMeetingRequestDTO request) {
        return fanMeetingService.createFanMeeting(request);
    }

    @ApiOperation(value = "구독한 인플루언서의 팬미팅 목록 조회", notes = "사용자가 구독한 인플루언서들의 팬미팅 목록을 조회합니다.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "구독한 인플루언서 팬미팅 목록 조회 성공"),
            @ApiResponse(code = 401, message = "인증 필요"),
            @ApiResponse(code = 500, message = "서버 오류")
    })
    @GetMapping("/subscribed")
    public List<FanMeetingResponseDTO> getSubscribedInfluencerMeetings(
            @ApiParam(value = "사용자 등급 (GENERAL, WHITE, SILVER, GOLD, VIP)", example = "GENERAL")
            @RequestParam(required = false) String grade,
            Authentication authentication) {
        CustomUserPrincipal principal = (CustomUserPrincipal) authentication.getPrincipal();
        Long userId = principal.getUserId();
        return fanMeetingService.getSubscribedInfluencerMeetings(grade != null ? grade : "GENERAL", userId);
    }

    @ApiOperation(value = "구독하지 않은 인플루언서의 팬미팅 목록 조회", notes = "사용자가 구독하지 않은 인플루언서들의 팬미팅 목록을 조회합니다.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "구독하지 않은 인플루언서 팬미팅 목록 조회 성공"),
            @ApiResponse(code = 401, message = "인증 필요"),
            @ApiResponse(code = 500, message = "서버 오류")
    })
    @GetMapping("/non-subscribed")
    public List<FanMeetingResponseDTO> getNonSubscribedInfluencerMeetings(
            @ApiParam(value = "사용자 등급 (GENERAL, WHITE, SILVER, GOLD, VIP)", example = "GENERAL")
            @RequestParam(required = false) String grade,
            Authentication authentication) {
        CustomUserPrincipal principal = (CustomUserPrincipal) authentication.getPrincipal();
        Long userId = principal.getUserId();
        return fanMeetingService.getNonSubscribedInfluencerMeetings(grade != null ? grade : "GENERAL", userId);
    }
}
