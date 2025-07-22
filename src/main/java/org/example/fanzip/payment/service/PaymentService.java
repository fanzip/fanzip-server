package org.example.fanzip.payment.service;
import org.example.fanzip.payment.dto.PaymentsRequestDto;
import org.example.fanzip.payment.dto.PaymentsResponseDto;

public interface PaymentService {
    PaymentsResponseDto createPayment(PaymentsRequestDto requestDto); // 결제 생성
    PaymentsResponseDto approvePaymentById(Long paymentId); // 결제 승인
    PaymentsResponseDto getPayment(Long paymentId);
}