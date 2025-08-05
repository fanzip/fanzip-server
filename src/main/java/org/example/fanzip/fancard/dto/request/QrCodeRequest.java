package org.example.fanzip.fancard.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class QrCodeRequest {
    private Long reservationId;
    private Long userId;
    private Long fanMeetingId;
    private Double latitude;
    private Double longitude;
    private String deviceInfo;
}