package org.example.fanzip.payment.service;

import lombok.RequiredArgsConstructor;
import org.example.fanzip.payment.dto.PaymentsRequestDto;
import org.example.fanzip.payment.dto.PaymentsResponseDto;
import org.example.fanzip.payment.mapper.PaymentsMapper;
import org.springframework.stereotype.Service;
import org.example.fanzip.payment.domain.Payments;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class PaymentServiceImpl implements PaymentService{
    private final PaymentsMapper paymentsMapper;

    @Override
    public PaymentsResponseDto createPayment(PaymentsRequestDto requestDto) {
        // RequestDto → Domain 변환
        Payments payments = requestDto.toEntity();

        // Mapper에 저장
        paymentsMapper.insertPayment(payments);

        // Domain → ResponseDto 반환
        return PaymentsResponseDto.from(payments);
    }


    @Override
    public PaymentsResponseDto getPayment(Long paymentId) {
        Payments payments = paymentsMapper.selectPayment(paymentId);
        return PaymentsResponseDto.from(payments);
    }
}
