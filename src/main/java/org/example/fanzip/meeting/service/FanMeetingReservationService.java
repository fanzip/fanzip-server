package org.example.fanzip.meeting.service;

import org.example.fanzip.meeting.dto.FanMeetingReservationResponseDTO;

public interface FanMeetingReservationService {
    FanMeetingReservationResponseDTO reserveSeat(Long meetingId, Long seatId, Long userId);
    void cancelReservation(Long meetingId, Long userId);
    boolean hasReserved(Long meetingId, Long userId);
}
