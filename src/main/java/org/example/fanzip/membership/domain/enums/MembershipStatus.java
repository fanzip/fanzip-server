package org.example.fanzip.membership.domain.enums;

public enum  MembershipStatus {
    PENDING,    // 결제 대기 상태
    ACTIVE,     // 구독 활성화
    CANCELLED,  // 결제 실패
    FAILED      // 유저 취소
}
