package org.example.fanzip.membership.dto;

import lombok.*;
import org.example.fanzip.membership.domain.MembershipVO;
import org.example.fanzip.membership.domain.enums.MembershipStatus;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MembershipSubscribeRequestDTO {
    private long influencerId;
    private int gradeId;
    private boolean autoRenewal;

    // 서버에서 금액을 다시 조회하므로 프론트 값은 무시됨(호환 유지용)
    private BigDecimal monthlyAmount;

    // 🔹 프론트에서 보내는 결제수단 문자열 (예: "KAKAOPAY", "kbpay", "tossPay")
    private String payMethod;

    /**
     * 주의: 결제 완료 전 단계에서는 구독을 PENDING으로 만들거나
     * 이 메서드를 사용하지 않는 것이 안전하다.
     * 꼭 필요하면 최소 정보만 PENDING으로 생성하도록 유지.
     */
    public MembershipVO toEntity(long userId) {
        LocalDate now = LocalDate.now();

        return MembershipVO.builder()
                .userId(userId)
                .influencerId(this.influencerId)
                .gradeId(this.gradeId)
                .status(MembershipStatus.PENDING) // 결제 전에는 PENDING
                .subscriptionStart(now)
                .subscriptionEnd(null) // 결제 성공 후 ACTIVE 전환 시 end 설정
                // 서버가 금액을 다시 정함: monthlyAmount/totalPaidAmount는 채우지 않거나 0 처리 권장
                .monthlyAmount(null)
                .totalPaidAmount(null)
                .autoRenewal(this.autoRenewal)
                .build();
    }
}
