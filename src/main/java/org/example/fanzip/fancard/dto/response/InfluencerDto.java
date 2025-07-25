package org.example.fanzip.fancard.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InfluencerDto {
    private Long influencerId;
    private String category;
    private String profileImage;
    private Boolean isVerified;
}