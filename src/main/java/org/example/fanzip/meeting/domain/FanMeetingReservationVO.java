package org.example.fanzip.meeting.domain;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class FanMeetingReservationVO {
    private Long reservationId;
    private Long meetingId;
    private Long userId;
    private Long seatId;
    private String reservationNumber;
    private String qrCode;
    private ReservationStatus status;
    private LocalDateTime reservedAt;
    private LocalDateTime cancelledAt;
    private LocalDateTime usedAt;


}
