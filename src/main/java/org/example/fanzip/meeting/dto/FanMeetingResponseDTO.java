package org.example.fanzip.meeting.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.fanzip.meeting.domain.FanMeetingStatus;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FanMeetingResponseDTO {
    private Long meetingId;
    private String influencerName;
    private String title;
    private String venueName;
    private String venueAddress;
    private LocalDateTime meetingDate;
    private int availableSeats;
    private FanMeetingStatus status;
    private LocalDateTime openTime;
    private String posterImageUrl;
    private String profileImageUrl;
}
