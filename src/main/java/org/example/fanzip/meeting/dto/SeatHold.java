package org.example.fanzip.meeting.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SeatHold implements Serializable {
    private Long userId;
    private Long meetingId;
    private Integer version;
}
