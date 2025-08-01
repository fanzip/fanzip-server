package org.example.fanzip.meeting.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.fanzip.meeting.domain.ReservationStatus;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FanMeetingReservationResponseDTO {
    private Long reservationId;
    private String reservationNumber;
    private String qrCode;
    private ReservationStatus status;
    private Long seatId;
}
