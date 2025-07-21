package org.example.fanzip.service;

import org.example.fanzip.dto.PaymentsDto;

public interface PaymentService {
    PaymentsDto createdPayment(PaymentsDto paymentsDto);
    PaymentsDto getPayment(Long paymentId);
}