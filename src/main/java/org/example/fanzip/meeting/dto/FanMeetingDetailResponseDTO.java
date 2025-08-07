package org.example.fanzip.meeting.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.fanzip.meeting.domain.FanMeetingStatus;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FanMeetingDetailResponseDTO {
    private Long meetingId;
    private String title;
    private String description;
    private String venueName;
    private String venueAddress;

    private LocalDateTime meetingDate;
    private int totalSeats;
    private int availableSeats;
    private FanMeetingStatus status;

    private LocalDateTime vipOpenTime;
    private LocalDateTime goldOpenTime;
    private LocalDateTime silverOpenTime;
    private LocalDateTime whiteOpenTime;
    private LocalDateTime generalOpenTime;

    private String posterImageUrl;
}
