package org.example.fanzip.meeting.mapper;

import org.example.fanzip.meeting.domain.FanMeetingVO;

import java.util.List;

public interface FanMeetingMapper {
    List<FanMeetingVO> findAllOpenMeetings();
    FanMeetingVO findById(Long meetingId);
}
