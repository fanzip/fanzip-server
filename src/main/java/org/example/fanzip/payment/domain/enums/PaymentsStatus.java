package org.example.fanzip.payment.domain.enums;

public enum PaymentsStatus {
    PENDING,    // 결제 요청됨
    PAID,       // 결제 완료
    CANCELLED,  // 유저 취소 or 유효 시간 만료
    FAILED,     // 결제 실패
    REFUNDED    // 환불 완료
}
