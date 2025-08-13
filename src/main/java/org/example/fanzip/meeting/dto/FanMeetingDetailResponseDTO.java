package org.example.fanzip.meeting.dto;

import lombok.*;
import org.example.fanzip.meeting.domain.FanMeetingStatus;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Setter
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

    private String profileImageUrl;
    private String posterImageUrl;
    private String influencerName;
    private Long influencerId;
}
