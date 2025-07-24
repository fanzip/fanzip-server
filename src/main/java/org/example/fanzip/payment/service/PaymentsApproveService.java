package org.example.fanzip.payment.service;

import lombok.RequiredArgsConstructor;
import org.example.fanzip.payment.domain.Payments;
import org.example.fanzip.payment.domain.enums.PaymentsStatus;
import org.example.fanzip.payment.dto.PaymentsResponseDto;
import org.example.fanzip.payment.repository.PaymentsRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class PaymentsApproveService {
    private final PaymentsRepository paymentsRepository;
    private final PaymentsRollbackService paymentsRollbackService;

    @Transactional
    public PaymentsResponseDto approvePaymentById(Long paymentId) {
        Payments payments = paymentsRepository.findById(paymentId);
        if (payments == null) {
            throw new IllegalArgumentException("해당 결제 정보를 찾을 수 없습니다. paymentId=" + paymentId);
        }
        validateStockAvailability(payments.getOrderId(), payments.getReservationId(), payments.getMembershipId()); // 결제 승인 시 재고 수량 검사 홤수
        // TODO : 주문 금액과 결제 요청 금액이 맞는지 로직 변경 필요
        BigDecimal expectedAmount = getExpectedAmountMock(payments);
        if (!payments.getAmount().equals(expectedAmount)) {
            throw new IllegalArgumentException("결제 요청 금액이 실제 금액과 일치하지 않습니다.");
        }
        payments.approve();
        paymentsRepository.updateStatus(paymentId, payments.getStatus());
//        if(true) throw new RuntimeException("강제 예외");
        /*  TODO: 멤버십 생성 or 갱신 로직 (Memberships 테이블 생기면 구현
        if (payments.getPaymentType() == PaymentType.MEMBERSHIP) {
         */
        return PaymentsResponseDto.from(payments);
    }

    @Transactional
    public PaymentsResponseDto failedPaymentById(Long paymentId) {
        Payments payments = paymentsRepository.findById(paymentId);
        if (payments.getStatus() != PaymentsStatus.PENDING) {
            throw new IllegalStateException("PENDING이 아닌 상태에서 실패 처리할 수 없습니다.");
        }
        payments.failed();
        paymentsRepository.updateStatus(paymentId, payments.getStatus());
        paymentsRollbackService.rollbackStock(payments);
        return PaymentsResponseDto.from(payments);
    }

    @Transactional
    public PaymentsResponseDto cancelledPaymentById(Long paymentId) {
        Payments payments = paymentsRepository.findById(paymentId);
        payments.cancel();
        paymentsRepository.updateStatus(paymentId, payments.getStatus());
        return PaymentsResponseDto.from(payments);
    }

    protected void validateStockAvailability(Long orderId, Long reservationId, Long membershipId) { // 결제 요청 시 재고 수량 검사 홤수
        if (orderId != null) {
            int mockStock = 10; // 임의 재고 수량, 실제 구현 시 각 Repository Mapper에서 findById() 호출 하기
            if (mockStock <= 0) {
                throw new IllegalStateException("상품 재고가 부족합니다");
            }
        }
        if (reservationId != null) { // 예매 가능 좌석
            int mockSeats = 5;
            if (mockSeats <= 0) {
                throw new IllegalStateException("예약 가능한 인원이 없습니다");
            }
        }
        if (membershipId != null) {
            boolean isMember = true; // 멤버십 가입된 사람
            if (!isMember) {
                throw new IllegalStateException("멤버십 가입 정보가 없습니다.");
            }
        }
    }

    private BigDecimal getExpectedAmountMock(Payments payments){
        if (payments.getOrderId() != null) {
            return new BigDecimal("38000"); // 주문 총 금액 mock
        }
        if (payments.getReservationId() != null) {
            return new BigDecimal("12000"); // 예매 금액 mock
        }
        if (payments.getMembershipId() != null) {
            return new BigDecimal("10000"); // 멤버십 월 구독료 mock
        }
        throw new IllegalStateException("결제 대상이 유효하지 않습니다.");
    }
}