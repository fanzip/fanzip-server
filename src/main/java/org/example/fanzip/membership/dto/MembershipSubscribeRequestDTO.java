package org.example.fanzip.membership.dto;

import lombok.*;
import org.example.fanzip.membership.domain.MembershipVO;


@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MembershipSubscribeRequestDTO {
    private long userId;
    private long influencerId;
    private int gradeId;
    private boolean autoRenewal;

    public MembershipVO toEntity(Long userId) {
        return MembershipVO.builder()
                .influencerId(this.influencerId)
                .gradeId(this.gradeId)
                .autoRenewal(this.autoRenewal)
                .build();
    }

}
