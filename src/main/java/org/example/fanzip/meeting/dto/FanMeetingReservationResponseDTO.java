package org.example.fanzip.meeting.dto;

import lombok.*;
import org.example.fanzip.meeting.domain.ReservationStatus;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FanMeetingReservationResponseDTO {
    private Long reservationId;
    private String reservationNumber;
    private ReservationStatus status;
    private Long seatId;
}
