package org.example.fanzip.payment.service;

import lombok.RequiredArgsConstructor;
import org.example.fanzip.global.exception.BusinessException;
import org.example.fanzip.global.exception.payment.PaymentErrorCode;
import org.example.fanzip.meeting.domain.FanMeetingReservationVO;
import org.example.fanzip.meeting.domain.FanMeetingSeatVO;
import org.example.fanzip.meeting.mapper.FanMeetingReservationMapper;
import org.example.fanzip.meeting.mapper.FanMeetingSeatMapper;
import org.example.fanzip.membership.mapper.MembershipMapper;
import org.example.fanzip.fancard.mapper.FancardMapper;
import org.example.fanzip.market.mapper.MarketOrderMapper;
import org.example.fanzip.membership.mapper.MembershipMapper;
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
import java.time.LocalDateTime;

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
    private final MembershipMapper membershipMapper;
    private final FancardService fancardService;
//    private final MarketOrderMapper marketOrderMapper;

    @Transactional
    public PaymentResponseDto approvePaymentById(Long paymentId) {
        Payments payments = paymentRepository.findById(paymentId);
        if (payments == null) {
            throw new BusinessException(PaymentErrorCode.PAYMENT_NOT_FOUND);
        }
        if (payments.getStatus() != PaymentStatus.PENDING) {
            throw new BusinessException(PaymentErrorCode.INVALID_STATUS);
        }
        paymentValidator.validateStockAvailability(payments.getOrderId(), payments.getReservationId(), payments.getMembershipId());

        // 실제 금액 검증
        BigDecimal expectedAmount = getExpectedAmount(payments);
        if (payments.getAmount().compareTo(expectedAmount) != 0) {
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

        // 예약 좌석인 경우 먼저 좌석 예약 처리
        if (payments.getPaymentType() == PaymentType.RESERVATION && payments.getReservationId() != null) {
            reserveSeat(payments.getReservationId());
        }

        // 좌석 예약이 성공한 후 결제 승인 처리
        payments.approve();
        paymentRepository.updateStatus(payments);

        // 멤버십 결제인 경우 멤버십 활성화 및 팬카드 생성
        if (payments.getPaymentType() == PaymentType.MEMBERSHIP && payments.getMembershipId() != null) {
            activateMembershipAndCreateFancard(payments.getMembershipId());
        }


        // 멤버십 결제 승인 시 추가 처리
        if (payments.getPaymentType() == PaymentType.MEMBERSHIP && payments.getMembershipId() != null) {
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
                fancardService.createFancardForMembership(payments.getMembershipId(), membership.getInfluencerId());
                System.out.println("팬카드 생성 완료: membershipId=" + payments.getMembershipId());
            } catch (RuntimeException e) {
                System.err.println("팬카드 생성 실패: membershipId=" + payments.getMembershipId() + ", error=" + e.getMessage());
                throw new BusinessException(PaymentErrorCode.FANCARD_CREATION_FAILED);
            }
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
        if (payments.getOrderId() != null) {
            // ORDER 금액 검증 (임시로 결제 요청 금액 그대로 사용)
            // TODO: 실제 주문 금액 검증 구현 필요
            return payments.getAmount();
        }

        if (payments.getReservationId() != null) {
            // RESERVATION 테이블에서 실제 금액 조회
            FanMeetingReservationVO reservation = reservationMapper.findById(payments.getReservationId());
            if (reservation == null) {
                throw new BusinessException(PaymentErrorCode.PAYMENT_NOT_FOUND);
            }

            // 좌석 가격 조회
            FanMeetingSeatVO seat = seatMapper.findById(reservation.getSeatId());
            if (seat == null || seat.getPrice() == null) {
                throw new BusinessException(PaymentErrorCode.SEATS_UNAVAILABLE);
            }
            return seat.getPrice();
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
            return payments.getAmount();
        }

        throw new BusinessException(PaymentErrorCode.UNSUPPORTED_PAYMENT_TYPE);
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

    private void activateMembershipAndCreateFancard(Long membershipId) {
        try {
            // 1. 멤버십 상태를 ACTIVE로 변경
            membershipMapper.updateMembershipStatus(membershipId, "ACTIVE");
            System.out.println("✅ 멤버십 활성화 완료 - membershipId: " + membershipId);

            // 2. 이미 팬카드가 있는지 확인
            if (fancardMapper.existsByMembershipId(membershipId)) {
                System.out.println("⚠️ 이미 팬카드가 존재함 - membershipId: " + membershipId);
                return;
            }

            // 3. 팬카드 생성
            String cardNumber = generateCardNumber(membershipId);
            String cardDesignUrl = getDefaultCardDesignUrl();

            fancardMapper.insertFancard(membershipId, cardNumber, cardDesignUrl);
            System.out.println("✅ 팬카드 생성 완료 - membershipId: " + membershipId + ", cardNumber: " + cardNumber);

        } catch (Exception e) {
            System.err.println("❌ 멤버십 활성화/팬카드 생성 실패: " + e.getMessage());
            throw new BusinessException(PaymentErrorCode.PAYMENT_NOT_FOUND); // 적절한 에러 코드로 변경 필요
        }
    }

    private String generateCardNumber(Long membershipId) {
        // 카드 번호 생성 로직 (예: FC + membershipId + timestamp)
        long timestamp = System.currentTimeMillis() / 1000;
        return String.format("FC%06d%06d", membershipId, timestamp % 1000000);
    }

    private String getDefaultCardDesignUrl() {
        // 기본 팬카드 디자인 URL
        return "/images/fancard/default.png";
    }
}