package org.example.fanzip.payment.service;

import lombok.RequiredArgsConstructor;
import org.example.fanzip.payment.dto.PaymentsRequestDto;
import org.example.fanzip.payment.dto.PaymentsResponseDto;
import org.example.fanzip.payment.mapper.PaymentsMapper;
import org.example.fanzip.payment.repository.PaymentsRepository;
import org.springframework.stereotype.Service;
import org.example.fanzip.payment.domain.Payments;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class PaymentServiceImpl implements PaymentService{
    private final PaymentsRepository paymentsRepository;
    private final PaymentsMapper paymentsMapper;

    @Override
    public PaymentsResponseDto createPayment(PaymentsRequestDto requestDto) {
        validateForeignKey(requestDto); // 외래키 유효성 검사
        validateStockAvailability(requestDto); // 재고/좌석/멤버십 체크
        if (paymentsMapper.existsByTransactionId(requestDto.getTransactionId())) { // 중복 결제 체크
            throw new IllegalArgumentException("이미 처리된 결제입니다.");
        }
        Payments payments = requestDto.toEntity();
        paymentsRepository.save(payments);
//        if (true) throw new RuntimeException("트랜잭션 롤백 테스트");
        return PaymentsResponseDto.from(payments);
    }
    @Override
    public PaymentsResponseDto approvePaymentById(Long paymentId){
        Payments payments = paymentsRepository.findById(paymentId);
        validateStockAvailability(payments); // 결제 승인 시 재고 수량 검사 홤수
        payments.approve();
        paymentsRepository.updateStatus(paymentId, payments.getStatus());
//        if(true) throw new RuntimeException("강제 예외"); rollback 확인
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
    public PaymentsResponseDto cancelledPaymentById(Long paymentId){
        Payments payments = paymentsRepository.findById(paymentId);
        payments.cancel();
        paymentsRepository.updateStatus(paymentId, payments.getStatus());
        return PaymentsResponseDto.from(payments);
    }

    @Override
    public PaymentsResponseDto refundedPaymentById(Long paymentId){
        Payments payments = paymentsRepository.findById(paymentId);
        payments.refund();
        paymentsRepository.updateStatus(paymentId, payments.getStatus());
        return PaymentsResponseDto.from(payments);
    }
    @Override
    public PaymentsResponseDto getPayment(Long paymentId) {
        Payments payments = paymentsRepository.findById(paymentId);
        return PaymentsResponseDto.from(payments);
    }

    private void validateForeignKey(PaymentsRequestDto dto) { // orderId, reservationId, membershipId null 확인 함수
        int nonNullCount = 0;
        if (dto.getOrderId() != null) nonNullCount++;
        if (dto.getReservationId() != null) nonNullCount++;
        if (dto.getMembershipId() != null) nonNullCount++;

        if (nonNullCount != 1) { // 충족 X -> IllegalArgumentException 발생
            throw new IllegalArgumentException("orderId, reservationId, membershipId 중 정확히 하나만 존재해야 한다.");
        }
    }
    private void validateStockAvailability(PaymentsRequestDto dto){ // 결제 요청 시 재고 수량 검사 홤수
        if(dto.getOrderId() != null){
            int mockStock = 10; // 임의 재고 수량, 실제 구현 시 각 Repository Mapper에서 findById() 호출 하기
            if(mockStock <= 0){
                throw new IllegalStateException("상품 재고가 부족합니다");
            }
        }
        if(dto.getReservationId() != null){ // 예매 가능 좌석
            int mockSeats = 5;
            if(mockSeats <= 0){
                throw new IllegalStateException("예약 가능한 인원이 없습니다");
            }
        }
        if(dto.getMembershipId() != null){
            boolean isMember = true; // 멤버십 가입된 사람
            if(!isMember){
                throw new IllegalStateException("멤버십 가입 정보가 없습니다.");
            }
        }
    }
    private void validateStockAvailability(Payments payments){ // 결제 승인 시 재고 수량 검사 홤수
        if(payments.getOrderId() != null){
            int mockStock = 10; // 임의 재고 수량, 실제 구현 시 각 Repository Mapper에서 findById() 호출 하기
            if(mockStock <= 0){
                throw new IllegalStateException("상품 재고가 부족합니다");
            }
        }
        if(payments.getReservationId() != null){ // 예매 가능 좌석
            int mockSeats = 5;
            if(mockSeats <= 0){
                throw new IllegalStateException("예약 가능한 인원이 없습니다");
            }
        }
        if(payments.getMembershipId() != null){
            boolean isMember = true; // 멤버십 가입된 사람
            if(!isMember){
                throw new IllegalStateException("멤버십 가입 정보가 없습니다.");
            }
        }
    }
}
