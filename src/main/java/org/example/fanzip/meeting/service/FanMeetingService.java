package org.example.fanzip.meeting.service;

import org.example.fanzip.meeting.domain.FanMeetingVO;
import org.example.fanzip.meeting.dto.FanMeetingDetailResponseDTO;
import org.example.fanzip.meeting.dto.FanMeetingRequestDTO;
import org.example.fanzip.meeting.dto.FanMeetingResponseDTO;
import org.example.fanzip.meeting.dto.FanMeetingSeatResponseDTO;

import java.util.List;

public interface FanMeetingService {
    List<FanMeetingResponseDTO> getOpenMeetings(String userGrade);
    FanMeetingDetailResponseDTO getMeetingDetail(Long meetingId);
    List<FanMeetingSeatResponseDTO> getSeats(Long meetingId);
    void createFanMeeting(FanMeetingVO meeting);
    FanMeetingDetailResponseDTO createFanMeeting(FanMeetingRequestDTO request);

    /**
     * 구독중인 인플루언서의 팬미팅 조회
     */
    List<FanMeetingResponseDTO> getSubscribedInfluencerMeetings(String userGrade, Long userId);

    /**
     * 구독하지 않은 인플루언서의 팬미팅 조회
     */
    List<FanMeetingResponseDTO> getNonSubscribedInfluencerMeetings(String userGrade, Long userId);
}
