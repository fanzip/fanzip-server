package org.example.fanzip.payment.dto;


import lombok.Builder;
import lombok.Getter;
import org.example.fanzip.payment.domain.Payments;
import org.example.fanzip.payment.domain.enums.PaymentsMethod;
import org.example.fanzip.payment.domain.enums.PaymentsStatus;
import org.example.fanzip.payment.domain.enums.PaymentsType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
public class PaymentsResponseDto {
    private Long userId;
    private Long paymentId;
    private Long orderId;
    private Long reservationId;
    private Long membershipId;
    private PaymentsType paymentsType;
    private PaymentsMethod paymentsMethod;
    private BigDecimal amount;
    private PaymentsStatus status;
    private String transactionId;
    private LocalDateTime paidAt;
    private LocalDateTime cancelledAt;
    private LocalDateTime refundedAt;
    private LocalDateTime createdAt;
    public static PaymentsResponseDto from(Payments payments) {
        return PaymentsResponseDto.builder()
                .userId(payments.getUserId())
                .paymentId(payments.getPaymentId())
                .orderId(payments.getOrderId())
                .reservationId(payments.getReservationId())
                .membershipId(payments.getMembershipId())
                .paymentsType(payments.getPaymentsType())
                .paymentsMethod(payments.getPaymentsMethod())
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