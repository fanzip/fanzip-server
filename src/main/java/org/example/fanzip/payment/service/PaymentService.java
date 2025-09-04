package org.example.fanzip.payment.service;
import org.example.fanzip.payment.domain.Payments;
import org.example.fanzip.payment.dto.PaymentRequestDto;
import org.example.fanzip.payment.dto.PaymentResponseDto;

import java.util.List;

public interface PaymentService {
    PaymentResponseDto createPayment(PaymentRequestDto requestDto); // 결제 생성
    PaymentResponseDto approvePaymentById(Long paymentId); // 결제 승인
    PaymentResponseDto failedPaymentById(Long paymentId);
    PaymentResponseDto cancelledPaymentById(Long paymentId);
    PaymentResponseDto refundedPaymentById(Long paymentId);
    PaymentResponseDto getPayment(Long paymentId);
    List<PaymentResponseDto> getMyPayments(Long userId);
}