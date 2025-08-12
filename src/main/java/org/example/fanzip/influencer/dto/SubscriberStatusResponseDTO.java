package org.example.fanzip.influencer.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubscriberStatusResponseDTO {
    private Long influencerId;
    private Long totalSubscribers;
    private Long todayNetChange; // 증가 감소 반영한 총 구독 인원
}