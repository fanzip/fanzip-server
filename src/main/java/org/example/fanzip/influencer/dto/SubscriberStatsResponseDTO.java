package org.example.fanzip.influencer.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SubscriberStatsResponseDTO {

    private String date;
    private Long subscriberCount;
}