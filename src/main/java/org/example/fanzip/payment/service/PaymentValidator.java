package org.example.fanzip.payment.service;

import org.example.fanzip.global.exception.BusinessException;
import org.example.fanzip.global.exception.payment.PaymentErrorCode;
import org.springframework.stereotype.Service;

@Service
public class PaymentValidator {
    protected void validateStockAvailability(Long orderId, Long reservationId, Long membershipId) { // 결제 요청 시 재고 수량 검사 홤수
        if (orderId != null) {
            int mockStock = 10; // 임의 재고 수량, 실제 구현 시 각 Repository Mapper에서 findById() 호출 하기
            if (mockStock <= 0) {
                throw new BusinessException(PaymentErrorCode.ORDER_STOCK_UNAVAILABLE);
            }
        }
        if (reservationId != null) { // 예매 가능 좌석
            int mockSeats = 5;
            if (mockSeats <= 0) {
                throw new BusinessException(PaymentErrorCode.SEATS_UNAVAILABLE);
            }
        }
        if (membershipId != null) {
            boolean isMember = true; // 멤버십 가입된 사람
            if (!isMember) {
                throw new BusinessException(PaymentErrorCode.MEMBERSHIP_NOT_FOUND);
            }
        }
    }
}
