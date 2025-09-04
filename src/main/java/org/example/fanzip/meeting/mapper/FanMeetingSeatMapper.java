package org.example.fanzip.meeting.mapper;

import org.apache.ibatis.annotations.Param;
import org.example.fanzip.meeting.domain.FanMeetingSeatVO;
import org.example.fanzip.meeting.dto.FanMeetingSeatResponseDTO;

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

    List<FanMeetingSeatResponseDTO> findSeatsByMeetingId(@Param("meetingId") Long meetingId);
    void insertSeatList(List<FanMeetingSeatVO> seats);
}
