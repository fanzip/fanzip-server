package org.example.fanzip.meeting.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class FanMeetingRequestDTO {
    private Long influencerId;
    private String title;
    private String description;
    private LocalDateTime meetingDate;

    /** 프론트에서 팬미팅 오픈 시간만 입력 */
    private LocalDateTime generalOpenTime;

    /** 서버 계산용 - 클라이언트 입력 무시 */
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime vipOpenTime;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime goldOpenTime;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime silverOpenTime;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime whiteOpenTime;

    private String posterImageUrl;
}
