package org.example.fanzip.meeting.domain;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class FanMeetingSeatVO {
    private Long seatId;
    private Long meetingId;
    private String seatNumber;
    private BigDecimal price;
    private boolean reserved;
    private int version;
    private LocalDateTime createdAt;
}