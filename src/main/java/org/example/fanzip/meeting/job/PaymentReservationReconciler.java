package org.example.fanzip.meeting.job;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.fanzip.meeting.mapper.FanMeetingPaymentProbeMapper;
import org.example.fanzip.meeting.service.FanMeetingReservationService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;


@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentReservationReconciler {
    private final FanMeetingPaymentProbeMapper probeMapper;
    private final FanMeetingReservationService reservationService;

    // 운영 시 프로퍼티로 조정: fanzip.reconcile.batch-size, interval 등
    private static final int BATCH_SIZE = 200;

    // 3초마다 결제/예약 상태 동기화 (idempotent)
    @Scheduled(fixedDelayString = "${fanzip.reconcile.interval-ms:3000}")
    public void reconcile() {
        try {
            // 결제 성공 → 예약 확정
            List<Long> toConfirm = probeMapper.findPaidReservationPaymentIdsNeedingConfirm(BATCH_SIZE);
            for (Long paymentId : toConfirm) {
                try {
                    reservationService.confirmByPaymentId(paymentId);
                } catch (Exception e) {
                    log.warn("[Reconcile][CONFIRM] paymentId={} failed: {}", paymentId, e.getMessage());
                }
            }

            // 결제 실패/취소 → 예약 취소
            List<Long> toCancel = probeMapper.findFailedOrCancelledReservationPaymentIdsNeedingCancel(BATCH_SIZE);
            for (Long paymentId : toCancel) {
                try {
                    reservationService.cancelByPaymentId(paymentId);
                } catch (Exception e) {
                    log.warn("[Reconcile][CANCEL] paymentId={} failed: {}", paymentId, e.getMessage());
                }
            }
        } catch (Exception e) {
            log.error("[Reconcile] unexpected error", e);
        }
    }
}
