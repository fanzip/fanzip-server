package org.example.fanzip.payment.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.example.fanzip.payment.domain.enums.PaymentsMethod;
import org.example.fanzip.payment.domain.enums.PaymentsStatus;
import org.example.fanzip.payment.domain.enums.PaymentsType;

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
    private PaymentsType paymentsType;
    private PaymentsMethod paymentsMethod;
    private BigDecimal amount;
    private PaymentsStatus status;
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
        this.paymentsType = PaymentsType.valueOf(paymentType); // String -> enum
        this.paymentsMethod = PaymentsMethod.valueOf(paymentMethod);
        this.status = PaymentsStatus.valueOf(status);
        this.transactionId = transactionId;
        this.amount = amount;
        this.paidAt = paidAt;
        this.cancelledAt = cancelledAt;
        this.refundedAt = refundedAt;
        this.createdAt = createdAt;
        this.userId = userId;
    }
    public void approve(){
        if(this.status != PaymentsStatus.PENDING){
            throw new IllegalStateException("결제 대기 상태(PENDING)에서만 승인할 수 있습니다.");
        }
        updateStatus(PaymentsStatus.PAID);
        this.paidAt = LocalDateTime.now();
    }
    public void failed(){
        if(this.status != PaymentsStatus.PENDING){
            throw new IllegalStateException("결제 대기 상태(PENDING)에서만 실패 처리할 수 있습니다.");
        }
        this.status = PaymentsStatus.FAILED;
    }
    public void cancel(){
        if(this.status != PaymentsStatus.PENDING){
            throw new IllegalStateException("결제 대기 상태(PENDING)에서만 취소할 수 있습니다.");
        }
        updateStatus(PaymentsStatus.CANCELLED);
        this.cancelledAt = LocalDateTime.now();
    }
    public void refund(){
        if(this.status != PaymentsStatus.PAID){
            throw new IllegalStateException("결제 완료(PAID)에서만 환불 가능합니다");
        }
        updateStatus(PaymentsStatus.REFUNDED);
        this.refundedAt = LocalDateTime.now();
    }
    private void updateStatus(PaymentsStatus paymentsStatus){
        this.status = paymentsStatus;
    }
}

