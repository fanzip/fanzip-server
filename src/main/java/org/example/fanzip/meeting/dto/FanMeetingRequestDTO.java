package org.example.fanzip.meeting.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class FanMeetingRequestDTO {
    private String title;
    private String description;
    private String venueName;
    private String venueAddress;
    private LocalDateTime meetingDate;
    private LocalDateTime vipOpenTime;
    private LocalDateTime goldOpenTime;
    private LocalDateTime silverOpenTime;
    private LocalDateTime whiteOpenTime;
    private LocalDateTime generalOpenTime;
    private String profileImageUrl;
}
