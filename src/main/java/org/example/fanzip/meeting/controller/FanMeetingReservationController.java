package org.example.fanzip.meeting.controller;

import lombok.RequiredArgsConstructor;
import org.example.fanzip.meeting.dto.FanMeetingReservationResponseDTO;
import org.example.fanzip.meeting.dto.FanMeetingSeatResponseDTO;
import org.example.fanzip.meeting.service.FanMeetingReservationService;
import org.example.fanzip.meeting.service.FanMeetingService;
import org.example.fanzip.security.CustomUserPrincipal;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/fan-meetings")
public class FanMeetingReservationController {

    private final FanMeetingReservationService reservationService;
    private final FanMeetingService seatService;

    @PostMapping("/{meetingId}/seats/{seatId}/reservation")
    public FanMeetingReservationResponseDTO reserve(
            @PathVariable Long meetingId,
            @PathVariable Long seatId,
            Authentication authentication) {

        CustomUserPrincipal principal = (CustomUserPrincipal) authentication.getPrincipal();
        Long userId = principal.getUserId();

        return reservationService.reserveSeat(meetingId, seatId, userId);
    }

    @DeleteMapping("/{meetingId}/reservation")
    public ResponseEntity<Void> cancelReservation(
            @PathVariable Long meetingId,
            Authentication authentication) {

        CustomUserPrincipal principal = (CustomUserPrincipal) authentication.getPrincipal();
        Long userId = principal.getUserId();

        reservationService.cancelReservation(meetingId, userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{meetingId}/seats")
    public List<FanMeetingSeatResponseDTO> getSeats(@PathVariable Long meetingId) {
        return seatService.getSeats(meetingId);
    }

    @GetMapping("/{meetingId}/reservation/check")
    @ResponseBody
    public Map<String, Boolean> checkReservation(
            @PathVariable Long meetingId,
            Authentication authentication) {

        CustomUserPrincipal principal = (CustomUserPrincipal) authentication.getPrincipal();
        Long userId = principal.getUserId();

        boolean reserved = reservationService.hasReserved(meetingId, userId);
        Map<String, Boolean> response = new HashMap<>();
        response.put("reserved", reserved);
        return response;
    }

}