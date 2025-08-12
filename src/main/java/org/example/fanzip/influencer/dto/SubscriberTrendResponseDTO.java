package org.example.fanzip.influencer.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubscriberTrendResponseDTO {
    private String date;
    private Long totalSubscribers;
    private Long changeFromPrevious;
}