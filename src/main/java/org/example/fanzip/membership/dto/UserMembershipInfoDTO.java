package org.example.fanzip.membership.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.fanzip.meeting.domain.UserGrade;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserMembershipInfoDTO {
    private UserGrade userGrade;
    private UserMembershipSubscriptionDTO subscription;
    
    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UserMembershipSubscriptionDTO {
        private Long membershipId;
        private Long influencerId;
        private String influencerName;
        private Integer gradeId;
        private String gradeName;
        private String gradeColor;
        private boolean isActive;
    }
}