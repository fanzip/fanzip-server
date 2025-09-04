package org.example.fanzip.fancard.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class QrCodeValidationResponse {
    private boolean isValid;
    private String status;
    private String message;
    
    // QR 코드에서 추출된 정보
    private Long userId;
    private Long fanMeetingId;
    private Long reservationId;
    
    // 사용자 정보
    private String userName;
    private String userEmail;
    
    // 예약 정보
    private ReservationDto reservation;
    
    // 검증 시간
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime validatedAt;
    
    // 에러 정보
    private String errorCode;
    private String errorDetails;
}