package org.example.fanzip.payment.job;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.fanzip.market.service.MarketOrderService;
import org.example.fanzip.membership.mapper.MembershipMapper;
import org.example.fanzip.payment.domain.enums.PaymentStatus;
import org.example.fanzip.payment.repository.PaymentRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * PENDING 상태로 남아있는 결제/주문/멤버십 레코드를 주기적으로 정리하는 서비스
 * 
 * 정리 대상:
 * 1. 30분 이상 PENDING 상태인 payments → CANCELLED 처리
 * 2. 관련 PENDING 상태 orders → 삭제
 * 3. 관련 PENDING 상태 memberships → CANCELLED 처리
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentCleanupService {
    
    private final PaymentRepository paymentRepository;
    private final MarketOrderService marketOrderService;
    private final MembershipMapper membershipMapper;
    
    // 30분마다 실행 (운영 시 프로퍼티로 조정 가능)
    @Scheduled(fixedDelayString = "${fanzip.cleanup.interval-ms:1800000}")
    @Transactional
    public void cleanupPendingRecords() {
        try {
            log.info("[PaymentCleanup] 시작 - PENDING 레코드 정리");
            
            int cleanupCount = 0;
            
            // 1. 30분 이상 PENDING 상태인 payments 조회
            List<Long> expiredPaymentIds = paymentRepository.findExpiredPendingPaymentIds(30);
            
            for (Long paymentId : expiredPaymentIds) {
                try {
                    cleanupSinglePayment(paymentId);
                    cleanupCount++;
                } catch (Exception e) {
                    log.warn("[PaymentCleanup] paymentId={} 정리 실패: {}", paymentId, e.getMessage());
                }
            }
            
            log.info("[PaymentCleanup] 완료 - {}개 레코드 정리됨", cleanupCount);
            
        } catch (Exception e) {
            log.error("[PaymentCleanup] 예상치 못한 오류 발생", e);
        }
    }
    
    private void cleanupSinglePayment(Long paymentId) {
        var payment = paymentRepository.findById(paymentId);
        if (payment == null || payment.getStatus() != PaymentStatus.PENDING) {
            return; // 이미 처리되었거나 상태가 변경됨
        }
        
        // 결제 타입별 정리
        switch (payment.getPaymentType()) {
            case ORDER -> {
                if (payment.getOrderId() != null) {
                    try {
                        marketOrderService.cleanupAfterPaymentFailed(payment.getOrderId());
                        log.debug("[PaymentCleanup] 주문 정리 완료: orderId={}", payment.getOrderId());
                    } catch (Exception e) {
                        log.warn("[PaymentCleanup] 주문 정리 실패: orderId={}, error={}", payment.getOrderId(), e.getMessage());
                    }
                }
            }
            case MEMBERSHIP -> {
                if (payment.getMembershipId() != null) {
                    try {
                        membershipMapper.updateMembershipStatus(payment.getMembershipId(), "CANCELLED");
                        log.debug("[PaymentCleanup] 멤버십 정리 완료: membershipId={}", payment.getMembershipId());
                    } catch (Exception e) {
                        log.warn("[PaymentCleanup] 멤버십 정리 실패: membershipId={}, error={}", payment.getMembershipId(), e.getMessage());
                    }
                }
            }
            case RESERVATION -> {
                // 예약은 기존 reconciler에서 처리하므로 여기서는 스킵
                log.debug("[PaymentCleanup] 예약 결제는 reconciler에서 처리: paymentId={}", paymentId);
            }
        }
        
        // 결제 상태를 CANCELLED로 변경
        try {
            payment.cancel();
            paymentRepository.updateStatus(payment);
            log.debug("[PaymentCleanup] 결제 취소 완료: paymentId={}", paymentId);
        } catch (Exception e) {
            log.warn("[PaymentCleanup] 결제 취소 실패: paymentId={}, error={}", paymentId, e.getMessage());
        }
    }
}