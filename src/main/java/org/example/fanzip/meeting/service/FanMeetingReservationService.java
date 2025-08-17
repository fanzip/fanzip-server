package org.example.fanzip.meeting.service;

import org.example.fanzip.meeting.dto.FanMeetingReservationResponseDTO;
import org.example.fanzip.meeting.dto.FanMeetingSeatResponseDTO;
import org.example.fanzip.meeting.dto.PaymentIntentResponseDTO;

import java.util.List;

public interface FanMeetingReservationService {
    FanMeetingReservationResponseDTO reserveSeat(Long meetingId, Long seatId, Long userId);
    void cancelReservation(Long meetingId, Long userId);
    boolean hasReserved(Long meetingId, Long userId);
    PaymentIntentResponseDTO startPayment(Long meetingId, Long seatId, Long userId);
    void confirmByPaymentId(Long paymentId);  // 결제 승인 콜백에서 호출
    void cancelByPaymentId(Long paymentId);
    List<FanMeetingSeatResponseDTO> getPendingSeats(Long meetingId, Long userId);
    boolean hasUpcomingMeetingWithInfluencer(Long userId, Long influencerId);
    java.util.Map<String, Object> getUpcomingMeetingWithInfluencer(Long userId, Long influencerId);
}
