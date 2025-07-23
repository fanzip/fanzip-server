package org.example.fanzip.payment.service;

import lombok.RequiredArgsConstructor;
import org.example.fanzip.payment.domain.enums.PaymentStatus;
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
public class PaymentServiceImpl implements PaymentService{
    private final PaymentsRepository paymentsRepository;

    @Transactional
    @Override
    public PaymentsResponseDto createPayment(PaymentsRequestDto requestDto) {
        if(paymentsRepository.existsByTransactionId(requestDto.getTransactionId())){
            throw new IllegalStateException("이미 처리된 결제 입니다.");
        }
        validateForeignKey(requestDto); // 외래키 유효성 검사
        switch (requestDto.getPaymentType()) { // 결제 유형별 처리
            case MEMBERSHIP:
                if (paymentsRepository.existsMembershipPayment(requestDto.getUserId(), requestDto.getMembershipId())) { // 이미 구독중인지 검사
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
        paymentsRepository.save(payments);
//        if (true) throw new RuntimeException("트랜잭션 롤백 테스트");
        return PaymentsResponseDto.from(payments);
    }
    @Transactional
    @Override
    public PaymentsResponseDto approvePaymentById(Long paymentId){
        Payments payments = paymentsRepository.findById(paymentId);
        validateStockAvailability(payments.getOrderId(), payments.getReservationId(), payments.getMembershipId()); // 결제 승인 시 재고 수량 검사 홤수
        // TODO : 주문 금액과 결제 요청 금액이 맞는지 로직 변경 필요
        Long expectedAmount = getExpectedAmountMock(payments);
        if (!payments.getAmount().equals(expectedAmount)) {
            throw new IllegalArgumentException("결제 요청 금액이 실제 금액과 일치하지 않습니다.");
        }
        payments.approve();
        paymentsRepository.updateStatus(paymentId, payments.getStatus());
        // if(true) throw new RuntimeException("강제 예외"); rollback 확인
        /*  TODO: 멤버십 생성 or 갱신 로직 (Memberships 테이블 생기면 구현
        if (payments.getPaymentType() == PaymentType.MEMBERSHIP) {
         */
        return PaymentsResponseDto.from(payments);
    }

    @Transactional
    @Override
    public PaymentsResponseDto failedPaymentById(Long paymentId){
        Payments payments = paymentsRepository.findById(paymentId);
        if (payments.getStatus() != PaymentStatus.PENDING) {
            throw new IllegalStateException("PENDING이 아닌 상태에서 실패 처리할 수 없습니다.");
        }
        payments.failed();
        paymentsRepository.updateStatus(paymentId, payments.getStatus());
        rollbackStock(payments);
        return PaymentsResponseDto.from(payments);
    }

    @Transactional
    @Override
    public PaymentsResponseDto cancelledPaymentById(Long paymentId){
        Payments payments = paymentsRepository.findById(paymentId);
        payments.cancel();
        paymentsRepository.updateStatus(paymentId, payments.getStatus());
        return PaymentsResponseDto.from(payments);
    }

    @Transactional
    @Override
    public PaymentsResponseDto refundedPaymentById(Long paymentId){
        Payments payments = paymentsRepository.findById(paymentId);
        payments.refund();
        paymentsRepository.updateStatus(paymentId, payments.getStatus());
        rollbackStock(payments);
        return PaymentsResponseDto.from(payments);
    }

    @Transactional(readOnly = true)
    @Override
    public PaymentsResponseDto getPayment(Long paymentId) {
        Payments payments = paymentsRepository.findById(paymentId);
        return PaymentsResponseDto.from(payments);
    }

    @Transactional(readOnly = true)
    public List<PaymentsResponseDto> getMyPayments(Long userId){
        List<Payments> paymentsList = paymentsRepository.findByUserId(userId);
        return paymentsList.stream()
                .map(PaymentsResponseDto::from)
                .collect(Collectors.toList());
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
    private void validateStockAvailability(Long orderId, Long reservationId, Long membershipId){ // 결제 요청 시 재고 수량 검사 홤수
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
    private Long getExpectedAmountMock(Payments payments) {
        if (payments.getOrderId() != null) {
            return 38000L; // 주문 총 금액 mock
        }
        if (payments.getReservationId() != null) {
            return 12000L; // 예매 금액 mock
        }
        if (payments.getMembershipId() != null) {
            return 10000L; // 멤버십 월 구독료 mock
        }
        throw new IllegalStateException("결제 대상이 유효하지 않습니다.");
    }
    private void rollbackStock(Payments payments){
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
