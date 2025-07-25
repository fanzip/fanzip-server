package org.example.fanzip.payment.service;

import lombok.RequiredArgsConstructor;
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
            throw new IllegalStateException("이미 처리된 결제 입니다.");
        }
        validateForeignKey(requestDto); // 외래키 유효성 검사
        if (requestDto.getPaymentType() == null) {
            throw new IllegalArgumentException("결제 유형이 존재하지 않습니다.");
        }
        switch (requestDto.getPaymentType()) { // 결제 유형별 처리
            case MEMBERSHIP:
                if (paymentRepository.existsMembershipPayment(requestDto.getUserId(), requestDto.getMembershipId())) { // 이미 구독중인지 검사
                    throw new IllegalArgumentException("이미 구독 중인 멤버십입니다.");
                }
                break;
            case ORDER:
                validateStockAvailability(requestDto.getOrderId(), null, null);
                break;
            case RESERVATION:
                validateStockAvailability(null, requestDto.getReservationId(), null);
                break;
            default:
                throw new IllegalArgumentException("지원하지 않는 결제 유형입니다.");
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
            throw new IllegalArgumentException("orderId, reservationId, membershipId 중 정확히 하나만 존재해야 한다.");
        }
    }
    protected void validateStockAvailability(Long orderId, Long reservationId, Long membershipId){ // 결제 요청 시 재고 수량 검사 홤수
        if(orderId != null){
            int mockStock = 10; // 임의 재고 수량, 실제 구현 시 각 Repository Mapper에서 findById() 호출 하기
            if(mockStock <= 0){
                throw new IllegalStateException("상품 재고가 부족합니다");
            }
        }
        if(reservationId!= null){ // 예매 가능 좌석
            int mockSeats = 5;
            if(mockSeats <= 0){
                throw new IllegalStateException("예약 가능한 인원이 없습니다");
            }
        }
        if(membershipId != null){
            boolean isMember = true; // 멤버십 가입된 사람
            if(!isMember){
                throw new IllegalStateException("멤버십 가입 정보가 없습니다.");
            }
        }
    }
}
