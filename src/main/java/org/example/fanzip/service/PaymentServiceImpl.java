package org.example.fanzip.service;

import lombok.RequiredArgsConstructor;
import org.example.fanzip.dto.PaymentsDto;
import org.example.fanzip.mapper.PaymentsMapper;
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
