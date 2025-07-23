package org.example.fanzip.payment.service;
import org.example.fanzip.payment.dto.PaymentsRequestDto;
import org.example.fanzip.payment.dto.PaymentsResponseDto;

import java.util.List;

public interface PaymentsService {
    PaymentsResponseDto createPayment(PaymentsRequestDto requestDto); // 결제 생성
    PaymentsResponseDto approvePaymentById(Long paymentId); // 결제 승인
    PaymentsResponseDto failedPaymentById(Long paymentId);
    PaymentsResponseDto cancelledPaymentById(Long paymentId);
    PaymentsResponseDto refundedPaymentById(Long paymentId);
    PaymentsResponseDto getPayment(Long paymentId);
    List<PaymentsResponseDto> getMyPayments(Long userId);
}