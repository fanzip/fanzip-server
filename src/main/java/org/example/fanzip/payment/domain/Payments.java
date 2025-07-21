package org.example.fanzip.payment.domain;

import lombok.Builder;
import lombok.Getter;
import org.example.fanzip.payment.domain.enums.PaymentMethod;
import org.example.fanzip.payment.domain.enums.PaymentStatus;
import org.example.fanzip.payment.domain.enums.PaymentType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
public class Payments {
    private Long paymentId;
    private Long orderId;
    private Long reservationId;
    private Long membershipId;
    private PaymentType paymentType;
    private PaymentMethod paymentMethod;
    private BigDecimal amount;
    private PaymentStatus status;
    private String transactionId;
    private LocalDateTime paidAt;
    private LocalDateTime cancelledAt;
    private LocalDateTime refundedAt;
    private LocalDateTime createdAt;

}

