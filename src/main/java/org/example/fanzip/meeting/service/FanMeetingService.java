package org.example.fanzip.meeting.service;

import org.example.fanzip.meeting.dto.FanMeetingDetailDTO;
import org.example.fanzip.meeting.dto.FanMeetingResponseDTO;

import java.util.List;

public interface FanMeetingService {
    List<FanMeetingResponseDTO> getOpenMeetings(String userGrade);
    FanMeetingDetailDTO getMeetingDetail(Long meetingId);
}
