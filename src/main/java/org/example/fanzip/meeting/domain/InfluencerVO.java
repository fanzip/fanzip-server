package org.example.fanzip.meeting.domain;

import lombok.Data;

// 다음 PR에서 삭제 예정
@Data
public class InfluencerVO {
    private Long influencerId;       // influenceId → influencerId (ERD 기준)
    private String influencerName;   // name → influencerName (명확하게)
    private String profileImage;
}