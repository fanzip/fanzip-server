package org.example.fanzip.payment.dto;


import lombok.Builder;
import lombok.Getter;
import org.example.fanzip.payment.domain.Payments;
import org.example.fanzip.payment.domain.enums.PaymentMethod;
import org.example.fanzip.payment.domain.enums.PaymentStatus;
import org.example.fanzip.payment.domain.enums.PaymentType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
public class PaymentsResponseDto {
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
    public static PaymentsResponseDto from(Payments payments) {
        return PaymentsResponseDto.builder()
                .paymentId(payments.getPaymentId())
                .orderId(payments.getOrderId())
                .reservationId(payments.getReservationId())
                .membershipId(payments.getMembershipId())
                .paymentType(payments.getPaymentType())
                .paymentMethod(payments.getPaymentMethod())
                .amount(payments.getAmount())
                .status(payments.getStatus())
                .transactionId(payments.getTransactionId())
                .paidAt(payments.getPaidAt())
                .cancelledAt(payments.getCancelledAt())
                .refundedAt(payments.getRefundedAt())
                .createdAt(payments.getCreatedAt())
                .build();
    }
}