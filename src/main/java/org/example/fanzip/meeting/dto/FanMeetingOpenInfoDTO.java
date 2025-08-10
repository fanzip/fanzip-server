package org.example.fanzip.meeting.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.fanzip.meeting.domain.FanMeetingStatus;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class FanMeetingOpenInfoDTO {
    private FanMeetingStatus status;
    private LocalDateTime vipOpenTime;
    private LocalDateTime goldOpenTime;
    private LocalDateTime silverOpenTime;
    private LocalDateTime whiteOpenTime;
    private LocalDateTime generalOpenTime;
}
