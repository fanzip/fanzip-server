package org.example.fanzip.payment.service;

import lombok.RequiredArgsConstructor;
import org.example.fanzip.global.exception.BusinessException;
import org.example.fanzip.global.exception.payment.PaymentErrorCode;
import org.example.fanzip.payment.domain.Payments;
import org.example.fanzip.payment.dto.PaymentRequestDto;
import org.example.fanzip.payment.dto.PaymentResponseDto;
import org.example.fanzip.payment.repository.PaymentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PaymentCreationService {
    private final PaymentRepository paymentRepository;
    @Transactional
    public PaymentResponseDto createPayment(PaymentRequestDto requestDto) {
        if(paymentRepository.existsByTransactionId(requestDto.getTransactionId())){
            throw new BusinessException(PaymentErrorCode.DUPLICATE_TRANSACTION);
        }
        validateForeignKey(requestDto); // 외래키 유효성 검사
        if (requestDto.getPaymentType() == null) {
            throw new BusinessException(PaymentErrorCode.UNSUPPORTED_PAYMENT_TYPE);
        }
        switch (requestDto.getPaymentType()) { // 결제 유형별 처리
            case MEMBERSHIP:
                if (paymentRepository.existsMembershipPayment(requestDto.getUserId(), requestDto.getMembershipId())) { // 이미 구독중인지 검사
                    throw new BusinessException(PaymentErrorCode.ALREADY_SUBSCRIBED);
                }
                break;
            case ORDER:
                validateStockAvailability(requestDto.getOrderId(), null, null);
                break;
            case RESERVATION:
                validateStockAvailability(null, requestDto.getReservationId(), null);
                break;
            default:
                throw new BusinessException(PaymentErrorCode.UNSUPPORTED_PAYMENT_TYPE);
        }
        Payments payments = requestDto.toEntity();
        paymentRepository.save(payments);
        return PaymentResponseDto.from(payments);
    }


    protected void validateForeignKey(PaymentRequestDto dto) { // orderId, reservationId, membershipId null 확인 함수
        int nonNullCount = 0;
        if (dto.getOrderId() != null) nonNullCount++;
        if (dto.getReservationId() != null) nonNullCount++;
        if (dto.getMembershipId() != null) nonNullCount++;

        if (nonNullCount != 1) { // 충족 X -> IllegalArgumentException 발생
            throw new BusinessException(PaymentErrorCode.INVALID_TARGET_COUNT);
        }
    }
    protected void validateStockAvailability(Long orderId, Long reservationId, Long membershipId){ // 결제 요청 시 재고 수량 검사 홤수
        if(orderId != null){
            int mockStock = 10; // 임의 재고 수량, 실제 구현 시 각 Repository Mapper에서 findById() 호출 하기
            if(mockStock <= 0){
                throw new BusinessException(PaymentErrorCode.ORDER_STOCK_UNAVAILABLE);
            }
        }
        if(reservationId!= null){ // 예매 가능 좌석
            int mockSeats = 5;
            if(mockSeats <= 0){
                throw new BusinessException(PaymentErrorCode.SEATS_UNAVAILABLE);
            }
        }
        if(membershipId != null){
            boolean isMember = true; // 멤버십 가입된 사람
            if(!isMember){
                throw new BusinessException(PaymentErrorCode.MEMBERSHIP_NOT_FOUND);
            }
        }
    }
}
