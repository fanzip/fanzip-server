package org.example.fanzip.payment.service;

import lombok.RequiredArgsConstructor;
import org.example.fanzip.global.exception.BusinessException;
import org.example.fanzip.global.exception.payment.PaymentErrorCode;
import org.example.fanzip.payment.domain.Payments;
import org.example.fanzip.payment.dto.PaymentResponseDto;
import org.example.fanzip.payment.repository.PaymentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PaymentRollbackService {
    private final PaymentRepository paymentRepository;
    @Transactional
    public PaymentResponseDto refundedPaymentById(Long paymentId){
        Payments payments = paymentRepository.findById(paymentId);
        if (payments == null) {
            throw new BusinessException(PaymentErrorCode.PAYMENT_NOT_FOUND);
        }
        payments.refund();
        paymentRepository.updateStatus(payments);
        rollbackStock(payments);
        return PaymentResponseDto.from(payments);
    }
    public void rollbackStock(Payments payments){
        if(payments.getOrderId() != null){
            // orderMapper.restoreStock(payments.getOrderId(), 수량);
            System.out.println("주문 ID" + payments.getOrderId() + "재고 복원 합니다.");
        }
        if(payments.getReservationId() != null){
            // reservationMapper.restoreSeats(payments.getReservationId()), 수량);
            System.out.println("예약 ID" + payments.getReservationId() + "좌석 복원 합니다.");
        } // 멤버십은 복원 대상X
    }
}
