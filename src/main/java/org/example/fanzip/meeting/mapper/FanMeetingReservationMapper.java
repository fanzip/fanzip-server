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

    int updateStatusToCanceled(
            @Param("reservationId") Long reservationId,
            @Param("cancelledAt") LocalDateTime cancelledAt
    );

}
