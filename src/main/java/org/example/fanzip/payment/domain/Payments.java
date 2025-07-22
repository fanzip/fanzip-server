package org.example.fanzip.payment.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.example.fanzip.payment.domain.enums.PaymentMethod;
import org.example.fanzip.payment.domain.enums.PaymentStatus;
import org.example.fanzip.payment.domain.enums.PaymentType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
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
    public Payments(Long paymentId, Long orderId, Long reservationId, Long membershipId,
                    String paymentType, String paymentMethod, BigDecimal amount,
                    String status, String transactionId,
                    LocalDateTime paidAt, LocalDateTime cancelledAt,
                    LocalDateTime refundedAt, LocalDateTime createdAt) {

        this.paymentId = paymentId;
        this.orderId = orderId;
        this.reservationId = reservationId;
        this.membershipId = membershipId;
        this.paymentType = PaymentType.valueOf(paymentType); // String -> enum
        this.paymentMethod = PaymentMethod.valueOf(paymentMethod);
        this.status = PaymentStatus.valueOf(status);
        this.transactionId = transactionId;
        this.amount = amount;
        this.paidAt = paidAt;
        this.cancelledAt = cancelledAt;
        this.refundedAt = refundedAt;
        this.createdAt = createdAt;
    }
    public void approve(){
        this.status = PaymentStatus.PAID;
        this.paidAt = LocalDateTime.now();
    }
    public void failed(){
        this.status = PaymentStatus.FAILED;
    }
    public void cancel(){
        this.status = PaymentStatus.CANCELLED;
        this.cancelledAt = LocalDateTime.now();
    }
}

