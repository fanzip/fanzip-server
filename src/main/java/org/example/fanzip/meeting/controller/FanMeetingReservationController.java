package org.example.fanzip.meeting.controller;

import lombok.RequiredArgsConstructor;
import org.example.fanzip.meeting.dto.FanMeetingReservationResponseDTO;
import org.example.fanzip.meeting.service.FanMeetingReservationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/fan-meetings")
public class FanMeetingReservationController {

    private final FanMeetingReservationService reservationService;

    @PostMapping("/{meetingId}/seats/{seatId}/reservation")
    public FanMeetingReservationResponseDTO reserve(
            @PathVariable Long meetingId,
            @PathVariable Long seatId,
            HttpServletRequest request) {

        Long userId = (Long) request.getAttribute("userId");   // 인터셉터에서 넣어줌
        return reservationService.reserveSeat(meetingId, seatId, userId);
    }

    @DeleteMapping("/{meetingId}/reservation")
    public ResponseEntity<Void> cancelReservation(
            @PathVariable Long meetingId,
            HttpServletRequest request) {

        Long userId = (Long) request.getAttribute("userId");
        reservationService.cancelReservation(meetingId, userId);
        return ResponseEntity.noContent().build();
    }
}