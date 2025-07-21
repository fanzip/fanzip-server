package org.example.fanzip.payment.service;

import lombok.RequiredArgsConstructor;
import org.example.fanzip.payment.dto.PaymentsRequestDto;
import org.example.fanzip.payment.dto.PaymentsResponseDto;
import org.example.fanzip.payment.mapper.PaymentsMapper;
import org.example.fanzip.payment.repository.PaymentsRepository;
import org.springframework.stereotype.Service;
import org.example.fanzip.payment.domain.Payments;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class PaymentServiceImpl implements PaymentService{
    private final PaymentsRepository paymentsRepository;

    @Override
    public PaymentsResponseDto createPayment(PaymentsRequestDto requestDto) {
        Payments payments = requestDto.toEntity();
        paymentsRepository.save(payments);
        return PaymentsResponseDto.from(payments);
    }


    @Override
    public PaymentsResponseDto getPayment(Long paymentId) {
        Payments payments = paymentsRepository.findById(paymentId);
        return PaymentsResponseDto.from(payments);
    }
}
