package org.example.fanzip.fancard.constants;

import org.example.fanzip.fancard.dto.response.BenefitDto;

import java.math.BigDecimal;
import java.util.List;

public final class FancardConstants {
    
    private FancardConstants() {}
    
    // 테스트용 고정값들 (실제 운영에서는 DB나 설정 파일에서 관리)
    public static final class TestData {
        public static final Long TEST_INFLUENCER_ID = 1L;
        public static final String TEST_INFLUENCER_NAME = "테스트 인플루언서";
        public static final String TEST_CATEGORY = "BEAUTY";
        public static final String TEST_PROFILE_IMAGE = "https://example.com/profiles/test.jpg";
        public static final Boolean TEST_IS_VERIFIED = true;
        
        public static final Long TEST_GRADE_ID = 4L;
        public static final String TEST_GRADE_NAME = "VIP";
        public static final String TEST_GRADE_COLOR = "#8B008B";
        public static final String TEST_BENEFITS_DESCRIPTION = "VIP 등급 최고 혜택";
        
        public static final BigDecimal TEST_MONTHLY_AMOUNT = BigDecimal.valueOf(10000.00);
        public static final BigDecimal TEST_TOTAL_PAID_AMOUNT = BigDecimal.valueOf(120000.00);
        public static final String TEST_MEMBERSHIP_STATUS = "ACTIVE";
        public static final Boolean TEST_AUTO_RENEWAL = true;
        
        public static final String TEST_RESERVATION_NUMBER = "FM20250722001";
        public static final String TEST_MEETING_TITLE = "테스트 인플루언서 팬미팅 2025";
        public static final String TEST_VENUE_NAME = "올림픽공원 K-아트홀";
        public static final String TEST_SEAT_NUMBER = "A-15";
        
        public static final List<BenefitDto> TEST_BENEFITS = List.of(
            BenefitDto.builder()
                .benefitId(1L)
                .benefitType("DISCOUNT")
                .benefitName("상품 할인")
                .benefitValue("20%")
                .description("모든 굿즈 20% 할인")
                .isActive(true)
                .build(),
            BenefitDto.builder()
                .benefitId(2L)
                .benefitType("PRIORITY")
                .benefitName("우선 예매")
                .benefitValue("VIP_PRIORITY")
                .description("팬미팅 VIP 등급 우선 예매")
                .isActive(true)
                .build()
        );
    }
    
    public static final class QrCode {
        public static final String URL_PREFIX = "https://api.fanzip.com/qr/entry?code=";
        public static final String CODE_PREFIX = "ENTRY_USER";
        public static final String CODE_SEPARATOR = "_";
        public static final String STATUS_ACTIVE = "ACTIVE";
        public static final int EXPIRY_SECONDS = 30;
        public static final String STATUS_LOCATION_ERROR = "LOCATION_ERROR";
        
        // QR 코드 검증 상태
        public static final String VALIDATION_SUCCESS = "SUCCESS";
        public static final String VALIDATION_EXPIRED = "EXPIRED";
        public static final String VALIDATION_INVALID_FORMAT = "INVALID_FORMAT";
        public static final String VALIDATION_INVALID_USER = "INVALID_USER";
        public static final String VALIDATION_INVALID_MEETING = "INVALID_MEETING";
        public static final String VALIDATION_INVALID_RESERVATION = "INVALID_RESERVATION";
        public static final String VALIDATION_LOCATION_ERROR = "LOCATION_ERROR";
        public static final String VALIDATION_ALREADY_USED = "ALREADY_USED";
        
        // QR 코드 포맷
        public static final String FANZIP_PREFIX = "FANZIP_";
        public static final int QR_DATA_PARTS = 4; // userId, fanMeetingId, reservationId, timestamp
        
        // 메시지
        public static final String SUCCESS_MESSAGE = "입장하세요! 팬미팅을 즐겨보세요 🎉";
        public static final String EXPIRED_MESSAGE = "QR 코드가 만료되었습니다. 새로운 QR 코드를 생성해주세요.";
        public static final String INVALID_FORMAT_MESSAGE = "올바르지 않은 QR 코드 형식입니다.";
        public static final String INVALID_USER_MESSAGE = "유효하지 않은 사용자입니다.";
        public static final String INVALID_MEETING_MESSAGE = "유효하지 않은 팬미팅입니다.";
        public static final String INVALID_RESERVATION_MESSAGE = "유효하지 않은 예약입니다.";
        public static final String LOCATION_ERROR_MESSAGE = "행사장 범위 밖에서는 입장할 수 없습니다.";
        public static final String ALREADY_USED_MESSAGE = "이미 사용된 QR 코드입니다.";
    }
}