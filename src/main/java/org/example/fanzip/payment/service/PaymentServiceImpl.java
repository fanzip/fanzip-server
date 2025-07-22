package org.example.fanzip.payment.service;

import lombok.RequiredArgsConstructor;
import org.example.fanzip.payment.dto.PaymentsRequestDto;
import org.example.fanzip.payment.dto.PaymentsResponseDto;
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
        validateForeignKey(requestDto);
        Payments payments = requestDto.toEntity();
        paymentsRepository.save(payments);
        return PaymentsResponseDto.from(payments);
    }
    @Override
    public PaymentsResponseDto approvePaymentById(Long paymentId){
        Payments payments = paymentsRepository.findById(paymentId);
        payments.approve();
        paymentsRepository.updateStatus(paymentId, payments.getStatus());
        return PaymentsResponseDto.from(payments);
    }

    @Override
    public PaymentsResponseDto failedPaymentById(Long paymentId){
        Payments payments = paymentsRepository.findById(paymentId);
        payments.failed();
        paymentsRepository.updateStatus(paymentId, payments.getStatus());
        return PaymentsResponseDto.from(payments);
    }

    @Override
    public PaymentsResponseDto getPayment(Long paymentId) {
        Payments payments = paymentsRepository.findById(paymentId);
        return PaymentsResponseDto.from(payments);
    }
    /*
        orderId, reservationId, membershipId null 확인 함수
        충족 X -> IllegalArgumentException 발생
     */
    private void validateForeignKey(PaymentsRequestDto dto) {
        int nonNullCount = 0;
        if (dto.getOrderId() != null) nonNullCount++;
        if (dto.getReservationId() != null) nonNullCount++;
        if (dto.getMembershipId() != null) nonNullCount++;

        if (nonNullCount != 1) {
            throw new IllegalArgumentException("orderId, reservationId, membershipId 중 정확히 하나만 존재해야 한다.");
        }
    }

}
