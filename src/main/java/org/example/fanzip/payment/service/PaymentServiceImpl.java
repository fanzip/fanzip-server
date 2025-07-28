package org.example.fanzip.payment.service;

import lombok.RequiredArgsConstructor;
import org.example.fanzip.payment.dto.PaymentRequestDto;
import org.example.fanzip.payment.dto.PaymentResponseDto;
import org.example.fanzip.payment.repository.PaymentRepository;
import org.springframework.stereotype.Service;
import org.example.fanzip.payment.domain.Payments;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
    private final PaymentRepository paymentRepository;
    private final PaymentCreationService creationService;
    private final PaymentApproveService approveService;
    private final PaymentRollbackService rollbackService;

    @Override
    public PaymentResponseDto createPayment(PaymentRequestDto dto){
        return creationService.createPayment(dto);
    }

    @Override
    public PaymentResponseDto approvePaymentById(Long paymentId) {
        return approveService.approvePaymentById(paymentId);
    }

    @Override
    public PaymentResponseDto failedPaymentById(Long paymentId) {
        return approveService.failedPaymentById(paymentId);
    }

    @Override
    public PaymentResponseDto cancelledPaymentById(Long paymentId) {
        return approveService.cancelledPaymentById(paymentId);
    }

    @Override
    public PaymentResponseDto refundedPaymentById(Long paymentId) {
        return rollbackService.refundedPaymentById(paymentId);
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentResponseDto getPayment(Long paymentId) {
        Payments payments = paymentRepository.findById(paymentId);
        if (payments == null) {
            throw new IllegalArgumentException("해당 결제 정보를 찾을 수 없습니다. paymentId=" + paymentId);
        }
        return PaymentResponseDto.from(payments);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentResponseDto> getMyPayments(Long userId) {
        return paymentRepository.findByUserId(userId).stream()
                .map(PaymentResponseDto::from)
                .collect(Collectors.toList());
    }
}