package org.example.fanzip.meeting.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.fanzip.meeting.domain.FanMeetingReservationVO;

import java.time.LocalDateTime;

@Mapper
public interface FanMeetingReservationMapper {
    int insertReservation(FanMeetingReservationVO reservation);
    boolean existByUserAndMeeting(@Param("userId") Long userId, @Param("meetingId") Long meetingId);


    FanMeetingReservationVO findByUserAndMeeting(@Param("userId") Long userId, @Param("meetingId") Long meetingId);

    int updateReservationStatus(
            @Param("reservationId") Long reservationId,
            @Param("status") String status,
            @Param("usedAt") LocalDateTime usedAt
    );

    boolean existsByMeetingIdAndUserId(@Param("meetingId") Long meetingId, @Param("userId") Long userId);
    boolean existConfirmedByUserAndMeeting(@Param("userId") Long userId, @Param("meetingId") Long meetingId);

    // PENDING 또는 RESERVED 상태 중 하나라도 있으면 true 반환
    boolean existAnyReservationByUserAndMeeting(@Param("userId") Long userId, @Param("meetingId") Long meetingId);

    void upsertPending(@Param("meetingId") Long meetingId, @Param("seatId") Long seatId,
                       @Param("userId") Long userId, @Param("paymentId") Long paymentId,
                       @Param("expiresAt") LocalDateTime expiresAt);


    // 결제 시작 전에 예약 PENDING 생성 (useGeneratedKeys로 PK 채움)
    int insertPending(FanMeetingReservationVO vo);

    // 결제 승인 후 예약 확정
    int markConfirmed(@Param("reservationId") Long reservationId,
                      @Param("confirmedAt") LocalDateTime confirmedAt);

    // 결제 실패/취소 시 예약 취소
    int updateStatusToCancelled(@Param("reservationId") Long reservationId,
                               @Param("cancelledAt") LocalDateTime cancelledAt);

    // paymentId로 예약 찾기 (payments.reservation_id JOIN)
    FanMeetingReservationVO findByPaymentId(@Param("paymentId") Long paymentId);
    Long findIdByReservationNumber(@Param("reservationNumber") String reservationNumber);
    
    // reservationId로 예약 정보 조회
    FanMeetingReservationVO findById(@Param("reservationId") Long reservationId);
    
    // 사용자가 특정 인플루언서의 진행 예정 팬미팅에 예약했는지 확인
    boolean hasUpcomingMeetingWithInfluencer(@Param("userId") Long userId, @Param("influencerId") Long influencerId);
    
    // 사용자가 특정 인플루언서의 진행 예정 팬미팅 ID 조회
    Long findUpcomingMeetingIdWithInfluencer(@Param("userId") Long userId, @Param("influencerId") Long influencerId);
    
    // 사용자가 특정 인플루언서의 진행 예정 팬미팅 예약 ID 조회
    Long findUpcomingReservationIdWithInfluencer(@Param("userId") Long userId, @Param("influencerId") Long influencerId);
    
    // 사용자가 특정 인플루언서의 진행 예정 팬미팅 좌석 ID 조회
    Long findUpcomingSeatIdWithInfluencer(@Param("userId") Long userId, @Param("influencerId") Long influencerId);
}
