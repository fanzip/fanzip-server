package org.example.fanzip.payment.service;

import lombok.RequiredArgsConstructor;
import org.example.fanzip.global.exception.BusinessException;
import org.example.fanzip.global.exception.payment.PaymentErrorCode;
import org.example.fanzip.meeting.domain.FanMeetingReservationVO;
import org.example.fanzip.meeting.mapper.FanMeetingReservationMapper;
import org.example.fanzip.membership.domain.MembershipVO;
import org.example.fanzip.membership.mapper.MembershipMapper;
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
    private final MembershipMapper membershipMapper;
    private final FanMeetingReservationMapper reservationMapper;
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
        System.out.println("=== 결제 생성 시작 ===");
        System.out.println("요청 DTO - 타입: " + requestDto.getPaymentType() + 
                         ", membershipId: " + requestDto.getMembershipId() + 
                         ", reservationId: " + requestDto.getReservationId() + 
                         ", orderId: " + requestDto.getOrderId() + 
                         ", 기존 influencerId: " + requestDto.getInfluencerId());
        
        // influencer_id 자동 채우기
        Long influencerId = resolveInfluencerId(requestDto);
        System.out.println("조회된 influencer_id: " + influencerId);
        
        // influencerId가 있으면 새로운 DTO 생성하여 설정
        PaymentRequestDto finalDto = requestDto;
        if (influencerId != null && requestDto.getInfluencerId() == null) {
            finalDto = PaymentRequestDto.builder()
                    .userId(requestDto.getUserId())
                    .orderId(requestDto.getOrderId())
                    .reservationId(requestDto.getReservationId())
                    .membershipId(requestDto.getMembershipId())
                    .influencerId(influencerId)
                    .transactionId(requestDto.getTransactionId())
                    .paymentType(requestDto.getPaymentType())
                    .paymentMethod(requestDto.getPaymentMethod())
                    .amount(requestDto.getAmount())
                    .build();
            System.out.println("✅ influencer_id 자동 설정 완료: " + influencerId);
        } else {
            System.out.println("❌ influencer_id 설정 안됨 - influencerId: " + influencerId + ", 기존값: " + requestDto.getInfluencerId());
        }
        
        Payments payments = finalDto.toEntity();
        System.out.println("최종 엔티티 influencer_id: " + payments.getInfluencerId());
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

    private Long resolveInfluencerId(PaymentRequestDto requestDto) {
        System.out.println("🔍 resolveInfluencerId 시작");
        
        // 이미 influencer_id가 있으면 그대로 사용
        if (requestDto.getInfluencerId() != null) {
            System.out.println("이미 influencer_id 존재: " + requestDto.getInfluencerId());
            return requestDto.getInfluencerId();
        }

        System.out.println("매퍼 객체 확인 - membershipMapper: " + (membershipMapper != null ? "OK" : "NULL"));
        System.out.println("매퍼 객체 확인 - reservationMapper: " + (reservationMapper != null ? "OK" : "NULL"));

        try {
            switch (requestDto.getPaymentType()) {
                case MEMBERSHIP:
                    System.out.println("MEMBERSHIP 타입 처리, membershipId: " + requestDto.getMembershipId());
                    if (requestDto.getMembershipId() != null) {
                        MembershipVO membership = membershipMapper.findByMembershipId(requestDto.getMembershipId());
                        System.out.println("멤버십 조회 결과: " + (membership != null ? "성공" : "실패"));
                        if (membership != null) {
                            System.out.println("✅ 멤버십에서 influencer_id 조회: " + membership.getInfluencerId());
                            return membership.getInfluencerId();
                        }
                    }
                    break;

                case RESERVATION:
                    System.out.println("RESERVATION 타입 처리, reservationId: " + requestDto.getReservationId());
                    if (requestDto.getReservationId() != null) {
                        FanMeetingReservationVO reservation = reservationMapper.findById(requestDto.getReservationId());
                        System.out.println("예약 조회 결과: " + (reservation != null ? "성공" : "실패"));
                        if (reservation != null) {
                            System.out.println("✅ 예약에서 influencer_id 조회: " + reservation.getInfluencerId());
                            return reservation.getInfluencerId();
                        }
                    }
                    break;

                case ORDER:
                    System.out.println("ORDER 타입: influencer_id 조회 로직 미구현 - 기본값 1L 사용");
                    return 1L; // 임시로 기본값 사용

                default:
                    System.out.println("알 수 없는 결제 타입: " + requestDto.getPaymentType());
                    break;
            }
        } catch (Exception e) {
            System.err.println("❌ influencer_id 조회 중 오류: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("❌ influencer_id 조회 실패 - null 반환");
        return null; // 조회 실패 시 null 반환
    }
}
