package org.example.fanzip.payment.service;

import lombok.RequiredArgsConstructor;
import org.example.fanzip.payment.dto.PaymentsDto;
import org.example.fanzip.payment.mapper.PaymentsMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService{
    private final PaymentsMapper paymentsMapper;

    @Override
    public PaymentsDto createdPayment(PaymentsDto paymentsDto) {
        paymentsMapper.insertPayment(paymentsDto);
        return paymentsDto;
    }

    @Override
    public PaymentsDto getPayment(Long paymentId) {
        return paymentsMapper.selectPayment(paymentId);
    }
}
