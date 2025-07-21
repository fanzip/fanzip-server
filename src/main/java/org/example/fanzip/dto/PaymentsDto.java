package org.example.fanzip.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class PaymentsDto {
    private Long paymentId;
    private Long orderId;
    private Long reservationId;
    private Long membershipId;
    private PaymentType paymentType;
    private PaymentMethod paymentMethod;
    private BigDecimal amount;
    private Status status;
    private String transactionId;
    private LocalDateTime paidAt;
    private LocalDateTime cancelledAt;
    private LocalDateTime refundedAt;
    private LocalDateTime createdAt;
    public static enum PaymentType {
        ORDER, // 상품
        RESERVATION, // 팬미팅
        MEMBERSHIP // 멤버십 구독
    }
    public static enum PaymentMethod{
        KBPAY,     // KB Pay
        TOSSPAY,   // 토스페이
        KAKAOPAY   // 카카오페이
    }
    public static enum Status {
        PENDING,    // 결제 요청됨
        PAID,       // 결제 완료
        CANCELLED,  // 유저 취소 or 유효 시간 만료
        FAILED,     // 결제 실패
        REFUNDED    // 환불 완료
    }
}

