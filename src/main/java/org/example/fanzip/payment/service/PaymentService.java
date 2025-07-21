package org.example.fanzip.payment.service;

import org.example.fanzip.payment.dto.PaymentsDto;

public interface PaymentService {
    PaymentsDto createdPayment(PaymentsDto paymentsDto);
    PaymentsDto getPayment(Long paymentId);
}