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
public class QrCodeResponse {
    private String qrCode;
    private String qrCodeUrl;
    private String status;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime generatedAt;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime expiresAt;
    
    private ReservationDto reservation;
    
    private String fcmToken;
    
    // 모바일 티켓에서 필요한 추가 정보
    private InfluencerDto influencer;
    private String fancardImageUrl;
}