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
    private final PaymentValidator paymentValidator;
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
    protected void validateStockAvailability(Long orderId, Long reservationId, Long membershipId) {
        // PaymentValidator의 실제 검증 로직 사용
        paymentValidator.validateStockAvailability(orderId, reservationId, membershipId);
    }
}
