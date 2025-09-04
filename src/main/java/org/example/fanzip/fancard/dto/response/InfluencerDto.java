package org.example.fanzip.fancard.dto.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class InfluencerDto {
    private Long influencerId;
    private String influencerName;
    private String category;
    private String profileImage;
    private String fancardImage;
    private Boolean isVerified;
}