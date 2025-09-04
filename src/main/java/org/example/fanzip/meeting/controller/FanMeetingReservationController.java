package org.example.fanzip.meeting.controller;

import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import org.example.fanzip.meeting.dto.FanMeetingReservationResponseDTO;
import org.example.fanzip.meeting.dto.FanMeetingSeatResponseDTO;
import org.example.fanzip.meeting.dto.PaymentIntentResponseDTO;
import org.example.fanzip.meeting.service.FanMeetingReservationService;
import org.example.fanzip.meeting.service.FanMeetingService;
import org.example.fanzip.security.CustomUserPrincipal;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Api(tags = "Fan Meeting Reservation", description = "팬미팅 예약 관리 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/fan-meetings")
public class FanMeetingReservationController {

    private final FanMeetingReservationService reservationService;
    private final FanMeetingService seatService;

    @ApiOperation(value = "팬미팅 좌석 예약", notes = "지정된 팬미팅의 좌석을 예약합니다.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "좌석 예약 성공"),
            @ApiResponse(code = 400, message = "이미 예약된 좌석 또는 잘못된 요청"),
            @ApiResponse(code = 401, message = "인증 필요"),
            @ApiResponse(code = 404, message = "팬미팅 또는 좌석을 찾을 수 없음"),
            @ApiResponse(code = 500, message = "서버 오류")
    })
    @PostMapping("/{meetingId}/seats/{seatId}/reservation")
    public FanMeetingReservationResponseDTO reserve(
            @ApiParam(value = "팬미팅 ID", required = true, example = "1")
            @PathVariable Long meetingId,
            @ApiParam(value = "좌석 ID", required = true, example = "1")
            @PathVariable Long seatId,
            Authentication authentication) {

        CustomUserPrincipal principal = (CustomUserPrincipal) authentication.getPrincipal();
        Long userId = principal.getUserId();

        return reservationService.reserveSeat(meetingId, seatId, userId);
    }

    @ApiOperation(value = "팬미팅 예약 취소", notes = "사용자의 팬미팅 예약을 취소합니다.")
    @ApiResponses({
            @ApiResponse(code = 204, message = "예약 취소 성공"),
            @ApiResponse(code = 401, message = "인증 필요"),
            @ApiResponse(code = 404, message = "예약을 찾을 수 없음"),
            @ApiResponse(code = 500, message = "서버 오류")
    })
    @DeleteMapping("/{meetingId}/reservation")
    public ResponseEntity<Void> cancelReservation(
            @ApiParam(value = "팬미팅 ID", required = true, example = "1")
            @PathVariable Long meetingId,
            Authentication authentication) {

        CustomUserPrincipal principal = (CustomUserPrincipal) authentication.getPrincipal();
        Long userId = principal.getUserId();

        reservationService.cancelReservation(meetingId, userId);
        return ResponseEntity.noContent().build();
    }

    @ApiOperation(value = "팬미팅 좌석 목록 조회", notes = "특정 팬미팅의 모든 좌석 정보를 조회합니다.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "좌석 목록 조회 성공"),
            @ApiResponse(code = 404, message = "팬미팅을 찾을 수 없음"),
            @ApiResponse(code = 500, message = "서버 오류")
    })
    @GetMapping("/{meetingId}/seats")
    public List<FanMeetingSeatResponseDTO> getSeats(
            @ApiParam(value = "팬미팅 ID", required = true, example = "1")
            @PathVariable Long meetingId) {
        return seatService.getSeats(meetingId);
    }

    @ApiOperation(value = "팬미팅 예약 상태 확인", notes = "사용자가 해당 팬미팅에 예약을 했는지 확인합니다.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "예약 상태 확인 성공"),
            @ApiResponse(code = 401, message = "인증 필요"),
            @ApiResponse(code = 404, message = "팬미팅을 찾을 수 없음"),
            @ApiResponse(code = 500, message = "서버 오류")
    })
    @GetMapping("/{meetingId}/reservation/check")
    @ResponseBody
    public Map<String, Boolean> checkReservation(
            @ApiParam(value = "팬미팅 ID", required = true, example = "1")
            @PathVariable Long meetingId,
            Authentication authentication) {

        CustomUserPrincipal principal = (CustomUserPrincipal) authentication.getPrincipal();
        Long userId = principal.getUserId();

        boolean reserved = reservationService.hasReserved(meetingId, userId);
        Map<String, Boolean> response = new HashMap<>();
        response.put("reserved", reserved);
        return response;
    }

    @ApiOperation(value = "팬미팅 결제 시작", notes = "예약한 좌석에 대한 결제를 시작합니다.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "결제 시작 성공"),
            @ApiResponse(code = 400, message = "잘못된 요청 또는 이미 결제된 좌석"),
            @ApiResponse(code = 401, message = "인증 필요"),
            @ApiResponse(code = 404, message = "팬미팅 또는 좌석을 찾을 수 없음"),
            @ApiResponse(code = 500, message = "서버 오류")
    })
    @PostMapping("/{meetingId}/seats/{seatId}/start-payment")
    public ResponseEntity<PaymentIntentResponseDTO> startPayment(
            @ApiParam(value = "팬미팅 ID", required = true, example = "1")
            @PathVariable Long meetingId,
            @ApiParam(value = "좌석 ID", required = true, example = "1")
            @PathVariable Long seatId,
            Authentication authentication
    ) {
        CustomUserPrincipal principal = (CustomUserPrincipal) authentication.getPrincipal();
        Long userId = principal.getUserId();

        return ResponseEntity.ok(reservationService.startPayment(meetingId, seatId, userId));
    }

    @ApiOperation(value = "대기 중인 좌석 목록 조회", notes = "사용자가 결제 대기 중인 좌석 목록을 조회합니다.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "대기 중인 좌석 목록 조회 성공"),
            @ApiResponse(code = 401, message = "인증 필요"),
            @ApiResponse(code = 404, message = "팬미팅을 찾을 수 없음"),
            @ApiResponse(code = 500, message = "서버 오류")
    })
    @GetMapping("/{meetingId}/pending-seats")
    public List<FanMeetingSeatResponseDTO> getPendingSeats(
            @ApiParam(value = "팬미팅 ID", required = true, example = "1")
            @PathVariable Long meetingId,
            Authentication authentication) {
        CustomUserPrincipal principal = (CustomUserPrincipal) authentication.getPrincipal();
        Long userId = principal.getUserId();

        return reservationService.getPendingSeats(meetingId, userId);
    }
    
    @GetMapping("/user/{influencerId}/upcoming")
    public ResponseEntity<Map<String, Object>> getUserUpcomingMeetings(
            @PathVariable Long influencerId,
            Authentication authentication) {
        CustomUserPrincipal principal = (CustomUserPrincipal) authentication.getPrincipal();
        Long userId = principal.getUserId();
        
        Map<String, Object> meetingInfo = reservationService.getUpcomingMeetingWithInfluencer(userId, influencerId);
        return ResponseEntity.ok(meetingInfo);
    }
    
    @GetMapping("/user/upcoming")
    public ResponseEntity<Map<String, Object>> getUserAllUpcomingMeetings(
            Authentication authentication) {
        CustomUserPrincipal principal = (CustomUserPrincipal) authentication.getPrincipal();
        Long userId = principal.getUserId();
        
        Map<String, Object> meetingInfo = reservationService.getAnyUpcomingMeeting(userId);
        return ResponseEntity.ok(meetingInfo);
    }
}