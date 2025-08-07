package org.example.fanzip.meeting.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class FanMeetingSeatResponseDTO {
    private Long seatId;
    private String seatNumber;
    private BigDecimal price;
    private boolean reserved;
}