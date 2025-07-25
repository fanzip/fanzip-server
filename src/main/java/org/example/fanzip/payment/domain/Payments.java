package org.example.fanzip.payment.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.example.fanzip.payment.domain.enums.PaymentMethod;
import org.example.fanzip.payment.domain.enums.PaymentStatus;
import org.example.fanzip.payment.domain.enums.PaymentType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

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
    private Long userId;
    public Payments(Long paymentId, Long orderId, Long reservationId, Long membershipId,
                    String paymentType, String paymentMethod, BigDecimal amount,
                    String status, String transactionId,
                    LocalDateTime paidAt, LocalDateTime cancelledAt,
                    LocalDateTime refundedAt, LocalDateTime createdAt, Long userId) {

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
        this.userId = userId;
    }
    public void approve(){
        if(this.status != PaymentStatus.PENDING){
            throw new IllegalStateException("결제 대기 상태(PENDING)에서만 승인할 수 있습니다.");
        }
        updateStatus(PaymentStatus.PAID);
        this.paidAt = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
    }
    public void failed(){
        if(this.status != PaymentStatus.PENDING){
            throw new IllegalStateException("결제 대기 상태(PENDING)에서만 실패 처리할 수 있습니다.");
        }
        updateStatus(PaymentStatus.FAILED);
        this.status = PaymentStatus.FAILED;
    }
    public void cancel(){
        if(this.status != PaymentStatus.PENDING){
            throw new IllegalStateException("결제 대기 상태(PENDING)에서만 취소할 수 있습니다.");
        }
        updateStatus(PaymentStatus.CANCELLED);
        this.cancelledAt = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
    }
    public void refund(){
        if(this.status != PaymentStatus.PAID){
            throw new IllegalStateException("결제 완료(PAID)에서만 환불 가능합니다");
        }
        updateStatus(PaymentStatus.REFUNDED);
        this.refundedAt = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
    }
    private void updateStatus(PaymentStatus paymentStatus){
        this.status = paymentStatus;
    }
}

