package org.example.fanzip.meeting.domain;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class FanMeetingSeatVO {
    private Long seatId;
    private Long meetingId;
    private String seatNumber;
    private String seatType;
    private BigDecimal price;
    private boolean reserved;  // DB에 reserved 컬럼 있어야 함
    private int version;        // 낙관적 락용 버전 필드
    private LocalDateTime createdAt;
}