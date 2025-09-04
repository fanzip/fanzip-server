package org.example.fanzip.payment.service;

import lombok.RequiredArgsConstructor;
import org.example.fanzip.global.exception.BusinessException;
import org.example.fanzip.global.exception.payment.PaymentErrorCode;
import org.example.fanzip.meeting.domain.FanMeetingReservationVO;
import org.example.fanzip.meeting.domain.FanMeetingSeatVO;
import org.example.fanzip.meeting.mapper.FanMeetingSeatMapper;
import org.example.fanzip.meeting.mapper.FanMeetingReservationMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentValidator {
    private final FanMeetingReservationMapper reservationMapper;
    private final FanMeetingSeatMapper seatMapper;
    protected void validateStockAvailability(Long orderId, Long reservationId, Long membershipId) {
        if (orderId != null) {
            // TODO: ORDER 실제 재고 검증 구현
        }
        
        if (reservationId != null) {
            // RESERVATION 실제 좌석 상태 검증
            FanMeetingReservationVO reservation = findReservationById(reservationId);
            if (reservation == null) {
                throw new BusinessException(PaymentErrorCode.SEATS_UNAVAILABLE);
            }
            
            Long seatId = reservation.getSeatId();
            FanMeetingSeatVO seat = seatMapper.findById(seatId);
            if (seat == null || seat.isReserved()) {
                throw new BusinessException(PaymentErrorCode.SEATS_UNAVAILABLE);
            }
        }
        
        if (membershipId != null) {
            // MEMBERSHIP 검증 (임시로 통과 처리)
            // TODO: MEMBERSHIP 실제 검증 구현
        }
    }
    
    private FanMeetingReservationVO findReservationById(Long reservationId) {
        return reservationMapper.findById(reservationId);
    }

}
