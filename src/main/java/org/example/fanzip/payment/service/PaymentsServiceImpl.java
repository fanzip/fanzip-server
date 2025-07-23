package org.example.fanzip.payment.service;

import lombok.RequiredArgsConstructor;
import org.example.fanzip.payment.dto.PaymentsRequestDto;
import org.example.fanzip.payment.dto.PaymentsResponseDto;
import org.example.fanzip.payment.repository.PaymentsRepository;
import org.springframework.stereotype.Service;
import org.example.fanzip.payment.domain.Payments;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PaymentsServiceImpl implements PaymentsService {
    private final PaymentsRepository paymentsRepository;
    private final PaymentCreationService creationService;
    private final PaymentsApproveService approveService;
    private final PaymentsRollbackService rollbackService;

    @Override
    public PaymentsResponseDto createPayment(PaymentsRequestDto dto){
        return creationService.createPayment(dto);
    }

    @Override
    public PaymentsResponseDto approvePaymentById(Long paymentId) {
        return approveService.approvePaymentById(paymentId);
    }

    @Override
    public PaymentsResponseDto failedPaymentById(Long paymentId) {
        return approveService.failedPaymentById(paymentId);
    }

    @Override
    public PaymentsResponseDto cancelledPaymentById(Long paymentId) {
        return approveService.cancelledPaymentById(paymentId);
    }

    @Override
    public PaymentsResponseDto refundedPaymentById(Long paymentId) {
        return rollbackService.refundedPaymentById(paymentId);
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentsResponseDto getPayment(Long paymentId) {
        Payments payments = paymentsRepository.findById(paymentId);
        return PaymentsResponseDto.from(payments);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentsResponseDto> getMyPayments(Long userId) {
        return paymentsRepository.findByUserId(userId).stream()
                .map(PaymentsResponseDto::from)
                .collect(Collectors.toList());
    }
}