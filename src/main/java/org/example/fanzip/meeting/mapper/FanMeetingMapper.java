package org.example.fanzip.meeting.mapper;

import org.apache.ibatis.annotations.Param;
import org.example.fanzip.meeting.domain.FanMeetingVO;
import org.example.fanzip.meeting.dto.FanMeetingDetailResponseDTO;
import org.example.fanzip.meeting.dto.FanMeetingOpenInfoDTO;

import java.util.List;

public interface FanMeetingMapper {
    List<FanMeetingVO> findAllOpenMeetings();
    FanMeetingVO findById(Long meetingId);
    void insertFanMeeting(FanMeetingVO fanMeetingVO);
    FanMeetingDetailResponseDTO findDetailById(@Param("meetingId") Long meetingId);
    FanMeetingOpenInfoDTO findOpenInfo(@Param("meetingId") Long meetingId);
    int decrementAvailableSeats(@Param("meetingId") Long meetingId);
    int incrementAvailableSeats(@Param("meetingId") Long meetingId);

}
