package org.example.fanzip.meeting.controller;

import lombok.RequiredArgsConstructor;
import org.example.fanzip.meeting.dto.FanMeetingDetailDTO;
import org.example.fanzip.meeting.dto.FanMeetingResponseDTO;
import org.example.fanzip.meeting.service.FanMeetingService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/fan-meetings")
@RequiredArgsConstructor
public class FanMeetingController {

    private final FanMeetingService fanMeetingService;

    @GetMapping
    public List<FanMeetingResponseDTO> getFanMeetings(@RequestParam(required = false) String grade) {
        return fanMeetingService.getOpenMeetings(grade != null ? grade : "GENERAL");
    }

    @GetMapping("/{meetingId}")
    public FanMeetingDetailDTO getFanMeetingDetail(@PathVariable Long meetingId) {
        return fanMeetingService.getMeetingDetail(meetingId);
    }
}
