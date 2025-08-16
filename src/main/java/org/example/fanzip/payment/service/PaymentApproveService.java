package org.example.fanzip.payment.service;

import lombok.RequiredArgsConstructor;
import org.example.fanzip.global.exception.BusinessException;
import org.example.fanzip.global.exception.payment.PaymentErrorCode;
import org.example.fanzip.market.mapper.MarketOrderMapper;
import org.example.fanzip.membership.mapper.MembershipMapper;
import org.example.fanzip.membership.domain.MembershipVO;
import org.example.fanzip.fancard.service.FancardService;
import org.example.fanzip.meeting.domain.FanMeetingReservationVO;
import org.example.fanzip.meeting.domain.FanMeetingSeatVO;
import org.example.fanzip.meeting.mapper.FanMeetingReservationMapper;
import org.example.fanzip.meeting.mapper.FanMeetingSeatMapper;
import org.example.fanzip.membership.mapper.MembershipMapper;
import org.example.fanzip.fancard.mapper.FancardMapper;
import org.example.fanzip.membership.domain.MembershipVO;
import org.example.fanzip.fancard.service.FancardService;
import org.example.fanzip.payment.domain.Payments;
import org.example.fanzip.payment.domain.enums.PaymentStatus;
import org.example.fanzip.payment.domain.enums.PaymentType;
import org.example.fanzip.payment.dto.PaymentResponseDto;
import org.example.fanzip.payment.repository.PaymentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PaymentApproveService {
    private final PaymentRepository paymentRepository;
    private final PaymentRollbackService paymentRollbackService;
    private final PaymentValidator paymentValidator;
    private final FanMeetingReservationMapper reservationMapper;
    private final FanMeetingSeatMapper seatMapper;
    private final MembershipMapper membershipMapper;
    private final FancardMapper fancardMapper;
    private final FancardService fancardService;

    @Transactional
    public PaymentResponseDto approvePaymentById(Long paymentId) {
        Payments payments = paymentRepository.findById(paymentId);
        if (payments == null) {
            throw new BusinessException(PaymentErrorCode.PAYMENT_NOT_FOUND);
        }
        paymentValidator.validateStockAvailability(payments.getOrderId(), payments.getReservationId(), payments.getMembershipId()); // 결제 승인 시 재고 수량 검사 홤수
        // TODO : 주문 금액과 결제 요청 금액이 맞는지 로직 변경 필요
        BigDecimal expectedAmount = getExpectedAmountMock(payments);
        if (payments.getAmount().compareTo(expectedAmount) != 0)  {
            throw new BusinessException(PaymentErrorCode.AMOUNT_MISMATCH);
        }

        // 주문 금액으로 검증 (ORDERS)
//        BigDecimal expectedAmount;
//        if (payments.getOrderId() != null) {
//            Map<String, Object> row = marketOrderMapper.selectOrderForPayment(payments.getOrderId());
//            if (row == null) throw new BusinessException(PaymentErrorCode.ORDER_NOT_FOUND);
//            expectedAmount = (BigDecimal) row.get("finalAmount");
//        } else {
//            throw new BusinessException(PaymentErrorCode.UNSUPPORTED_PAYMENT_TYPE);
//        }
//
//        // 금액 검증
//        if (payments.getAmount().compareTo(expectedAmount) != 0)
//            throw new BusinessException(PaymentErrorCode.AMOUNT_MISMATCH);


        payments.approve();
        paymentRepository.updateStatus(payments);


        // 결제 타입별 후처리
        if (payments.getPaymentType() == PaymentType.MEMBERSHIP && payments.getMembershipId() != null) {
            handleMembershipPaymentApproval(payments);
        } else if (payments.getPaymentType() == PaymentType.RESERVATION && payments.getReservationId() != null) {
            handleReservationPaymentApproval(payments);
        } else if (payments.getPaymentType() == PaymentType.ORDER && payments.getOrderId() != null) {
            handleOrderPaymentApproval(payments);
        }

        return PaymentResponseDto.from(payments);
    }

    @Transactional
    public PaymentResponseDto failedPaymentById(Long paymentId) {
        Payments payments = paymentRepository.findById(paymentId);
        if (payments.getStatus() != PaymentStatus.PENDING) {
            throw new BusinessException(PaymentErrorCode.INVALID_STATUS);
        }

        // 예약 좌석인 경우 좌석 해제 처리
        if (payments.getPaymentType() == PaymentType.RESERVATION && payments.getReservationId() != null) {
            releaseSeat(payments.getReservationId());
        }

        payments.failed();
        paymentRepository.updateStatus(payments);
        paymentRollbackService.rollbackStock(payments);
        return PaymentResponseDto.from(payments);
    }

    @Transactional
    public PaymentResponseDto cancelledPaymentById(Long paymentId) {
        Payments payments = paymentRepository.findById(paymentId);

        // 예약 좌석인 경우 좌석 해제 처리
        if (payments.getPaymentType() == PaymentType.RESERVATION && payments.getReservationId() != null) {
            releaseSeat(payments.getReservationId());
        }

        payments.cancel();
        paymentRepository.updateStatus(payments);
        return PaymentResponseDto.from(payments);
    }

    private BigDecimal getExpectedAmount(Payments payments) {
        PaymentType type = payments.getPaymentType();
        if (type == null) {
            throw new BusinessException(PaymentErrorCode.PAYMENT_NOT_FOUND); // 테스트 메시지와 통일
        }

        switch (type) {
            case ORDER:
                // TODO: 주문 금액을 주문 테이블에서 재조회하는게 가장 안전
                return payments.getAmount();

            case RESERVATION: {
                if (payments.getReservationId() == null) {
                    throw new BusinessException(PaymentErrorCode.PAYMENT_NOT_FOUND);
                }
                FanMeetingReservationVO reservation = reservationMapper.findById(payments.getReservationId());
                if (reservation == null) throw new BusinessException(PaymentErrorCode.PAYMENT_NOT_FOUND);

                FanMeetingSeatVO seat = seatMapper.findById(reservation.getSeatId());
                if (seat == null || seat.getPrice() == null) {
                    throw new BusinessException(PaymentErrorCode.SEATS_UNAVAILABLE);
                }
                return seat.getPrice();
            }

            case MEMBERSHIP: {
                if (payments.getMembershipId() == null) {
                    throw new BusinessException(PaymentErrorCode.PAYMENT_NOT_FOUND);
                }
                // 멤버십 금액 정책에 맞게 금액을 산출
                MembershipVO membership = membershipMapper.findByMembershipId(payments.getMembershipId());
                if (membership == null) throw new BusinessException(PaymentErrorCode.MEMBERSHIP_NOT_FOUND);

                return payments.getAmount();
            }

            default:
                throw new BusinessException(PaymentErrorCode.UNSUPPORTED_PAYMENT_TYPE);
        }
    }

    private void reserveSeat(Long reservationId) {
        // 1. reservation_id로 seat_id 조회
        FanMeetingReservationVO reservation = reservationMapper.findById(reservationId);
        if (reservation == null) {
            throw new BusinessException(PaymentErrorCode.PAYMENT_NOT_FOUND);
        }

        Long seatId = reservation.getSeatId();

        // 2. 좌석 정보 조회 (version 포함)
        FanMeetingSeatVO seat = seatMapper.findById(seatId);
        if (seat == null) {
            throw new BusinessException(PaymentErrorCode.SEATS_UNAVAILABLE);
        }

        // 3. 이미 예약된 좌석인지 확인
        if (seat.isReserved()) {
            throw new BusinessException(PaymentErrorCode.SEATS_UNAVAILABLE);
        }

        // 4. 낙관적 락을 사용한 좌석 예약 처리
        int updated = seatMapper.updateSeatWithVersionCheck(seatId, true, seat.getVersion());
        if (updated == 0) {
            // version이 변경되었거나 이미 예약된 경우
            throw new BusinessException(PaymentErrorCode.SEATS_UNAVAILABLE);
        }

        System.out.println("✅ 좌석 예약 완료 - seatId: " + seatId + ", reservationId: " + reservationId + ", version: " + seat.getVersion());
    }

    private void releaseSeat(Long reservationId) {
        // 1. reservation_id로 seat_id 조회
        FanMeetingReservationVO reservation = reservationMapper.findById(reservationId);
        if (reservation == null) {
            throw new BusinessException(PaymentErrorCode.PAYMENT_NOT_FOUND);
        }

        Long seatId = reservation.getSeatId();

        // 2. 좌석을 사용 가능 상태로 변경
        int updated = seatMapper.updateSeatReservation(seatId, false);
        if (updated == 0) {
            System.out.println("⚠️ 좌석 해제 실패 - seatId: " + seatId + ", reservationId: " + reservationId);
        } else {
            System.out.println("🔓 좌석 해제 완료 - seatId: " + seatId + ", reservationId: " + reservationId);
        }
    }

    private void handleMembershipPaymentApproval(Payments payments) {
        // 1. 멤버십을 ACTIVE 상태로 변경
        int updateResult = membershipMapper.updateToActive(payments.getMembershipId());
        if (updateResult == 0) {
            throw new BusinessException(PaymentErrorCode.MEMBERSHIP_ACTIVATION_FAILED);
        }
        System.out.println("멤버십 상태를 ACTIVE로 변경: membershipId=" + payments.getMembershipId());

        // 2. 멤버십 정보 조회하여 인플루언서 ID 확인
        MembershipVO membership = membershipMapper.findByMembershipId(payments.getMembershipId());
        if (membership == null) {
            throw new BusinessException(PaymentErrorCode.MEMBERSHIP_NOT_FOUND);
        }

        // 3. 총 납입 금액 업데이트
        int updateAmountResult = membershipMapper.updateTotalPaidAmount(payments.getMembershipId(), payments.getAmount());
        if (updateAmountResult == 0) {
            System.err.println("총 납입 금액 업데이트 실패: membershipId=" + payments.getMembershipId());
        } else {
            System.out.println("총 납입 금액 업데이트 완료: membershipId=" + payments.getMembershipId() + ", amount=" + payments.getAmount());
        }

        // 4. 팬카드 자동 생성 (실패 시 예외 전파로 전체 트랜잭션 롤백)
        try {
            // 결제 요청에서 온 influencer_id를 우선 사용, 없으면 멤버십에서 조회
            Long influencerId = getInfluencerIdForPayment(payments, membership.getInfluencerId());
            fancardService.createFancardForMembership(payments.getMembershipId(), influencerId);
            System.out.println("팬카드 생성 완료: membershipId=" + payments.getMembershipId() + ", influencerId=" + influencerId);
        } catch (RuntimeException e) {
            System.err.println("팬카드 생성 실패: membershipId=" + payments.getMembershipId() + ", error=" + e.getMessage());
            throw new BusinessException(PaymentErrorCode.FANCARD_CREATION_FAILED);
        }
    }

    private void handleReservationPaymentApproval(Payments payments) {
        // 1. 예약 정보 조회
        FanMeetingReservationVO reservation = reservationMapper.findById(payments.getReservationId());
        if (reservation == null) {
            throw new BusinessException(PaymentErrorCode.PAYMENT_NOT_FOUND);
        }

        // 2. 예약 상태를 RESERVED로 변경
        reservationMapper.markConfirmed(payments.getReservationId(), java.time.LocalDateTime.now());
        System.out.println("예약 상태를 RESERVED로 변경: reservationId=" + payments.getReservationId());

        // 3. 로깅용으로만 influencer_id 확인
        Long influencerId = getInfluencerIdForPayment(payments, reservation.getInfluencerId());
        System.out.println("예약 결제 완료: reservationId=" + payments.getReservationId() + ", influencerId=" + influencerId);
    }

    private void handleOrderPaymentApproval(Payments payments) {
        // 현재는 주문 테이블이 구현되지 않아 간단히 처리
        // TODO: 실제 주문 테이블에서 influencer_id 조회 로직 구현 필요
        Long influencerId = getInfluencerIdForPayment(payments, null);
        System.out.println("주문 결제 완료: orderId=" + payments.getOrderId() + ", influencerId=" + influencerId);

        // 주문 결제에서는 팬카드 생성하지 않음 (다른 팀원 구현 영역)
    }

    private Long getInfluencerIdForPayment(Payments payments, Long fallbackInfluencerId) {
        // 결제 요청에서 온 influencer_id를 우선 사용
        if (payments.getInfluencerId() != null) {
            return payments.getInfluencerId();
        }

        // 없으면 관련 테이블에서 조회한 값 사용
        if (fallbackInfluencerId != null) {
            return fallbackInfluencerId;
        }

        // 그것도 없으면 기본값 사용 (임시)
        System.out.println("⚠️ influencer_id를 찾을 수 없어 기본값(1L) 사용: paymentId=" + payments.getPaymentId());
        return 1L;
        if (payments.getReservationId() != null) {
            return new BigDecimal("12000"); // 예매 금액 mock
        }
        if (payments.getMembershipId() != null) {
            try {
                // 실제 멤버십 금액 조회
                BigDecimal actualAmount = membershipMapper.findMonthlyAmountByGradeId(
                    membershipMapper.findByMembershipId(payments.getMembershipId()).getGradeId()
                );
                return actualAmount != null ? actualAmount : new BigDecimal("10000"); // fallback
            } catch (Exception e) {
                System.err.println("멤버십 금액 조회 실패, 기본값 사용: " + e.getMessage());
                return new BigDecimal("10000"); // fallback
            }
        }
        throw new BusinessException(PaymentErrorCode.UNSUPPORTED_PAYMENT_TYPE);
    }
}