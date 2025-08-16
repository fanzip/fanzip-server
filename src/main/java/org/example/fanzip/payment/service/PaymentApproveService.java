package org.example.fanzip.payment.service;

import lombok.RequiredArgsConstructor;
import org.example.fanzip.global.exception.BusinessException;
import org.example.fanzip.global.exception.payment.PaymentErrorCode;
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
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PaymentApproveService {
    private final PaymentRepository paymentRepository;
    private final PaymentRollbackService paymentRollbackService;
    private final PaymentValidator paymentValidator;
    private final MembershipMapper membershipMapper;
    private final FancardService fancardService;
//    private final MarketOrderMapper marketOrderMapper;

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
        payments.failed();
        paymentRepository.updateStatus(payments);
        paymentRollbackService.rollbackStock(payments);
        return PaymentResponseDto.from(payments);
    }

    @Transactional
    public PaymentResponseDto cancelledPaymentById(Long paymentId) {
        Payments payments = paymentRepository.findById(paymentId);
        payments.cancel();
        paymentRepository.updateStatus(payments);
        return PaymentResponseDto.from(payments);
    }

    private BigDecimal getExpectedAmountMock(Payments payments){
        if (payments.getOrderId() != null) {
            return new BigDecimal("38000"); // 주문 총 금액 mock
        }
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