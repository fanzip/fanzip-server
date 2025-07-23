package org.example.fanzip.payment.service;

import lombok.RequiredArgsConstructor;
import org.example.fanzip.payment.domain.Payments;
import org.example.fanzip.payment.dto.PaymentsResponseDto;
import org.example.fanzip.payment.repository.PaymentsRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PaymentsRollbackService {
    private final PaymentsRepository paymentsRepository;
    @Transactional
    public PaymentsResponseDto refundedPaymentById(Long paymentId){
        Payments payments = paymentsRepository.findById(paymentId);
        payments.refund();
        paymentsRepository.updateStatus(paymentId, payments.getStatus());
        rollbackStock(payments);
        return PaymentsResponseDto.from(payments);
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
