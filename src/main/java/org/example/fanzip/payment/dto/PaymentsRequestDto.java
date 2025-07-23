package org.example.fanzip.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.fanzip.payment.domain.Payments;
import org.example.fanzip.payment.domain.enums.PaymentsMethod;
import org.example.fanzip.payment.domain.enums.PaymentsStatus;
import org.example.fanzip.payment.domain.enums.PaymentsType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentsRequestDto {
    private Long userId;
    private Long orderId;
    private Long reservationId;
    private Long membershipId;
    private String transactionId;
    private PaymentsType paymentsType;
    private PaymentsMethod paymentsMethod;
    private BigDecimal amount;
    public Payments toEntity() {
        return Payments.builder()
                .userId(userId)
                .orderId(orderId)
                .reservationId(reservationId)
                .membershipId(membershipId)
                .transactionId(transactionId)
                .paymentsType(paymentsType)
                .paymentsMethod(paymentsMethod)
                .amount(amount)
                .status(PaymentsStatus.PENDING) // 기본값 설정
                .createdAt(LocalDateTime.now()) // 생성 시간 자동 할당
                .build();
    }
}
