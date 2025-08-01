package org.example.fanzip.meeting.mapper;

import org.apache.ibatis.annotations.Param;
import org.example.fanzip.meeting.domain.FanMeetingSeatVO;

import java.util.List;

public interface FanMeetingSeatMapper {
    List<FanMeetingSeatVO> findByMeetingId(@Param("meetingId") Long meetingId);
    int updateSeatReservation(@Param("seatId") Long seatId, @Param("reserved") boolean reserved);
    // 낙관적 락 테스트
    FanMeetingSeatVO findById(@Param("seatId") Long seatId);
    int updateSeatWithVersionCheck(
            @Param("seatId") Long seatId,
            @Param("reserved") boolean reserved,
            @Param("version") int version
    );

}
