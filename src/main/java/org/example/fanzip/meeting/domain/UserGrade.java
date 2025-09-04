package org.example.fanzip.meeting.domain;

import java.util.Arrays;

public enum UserGrade {
    VIP,
    GOLD,
    SILVER,
    WHITE,
    GENERAL;

    public static UserGrade from(String value) {
        return Arrays.stream(UserGrade.values())
                .filter(g -> g.name().equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 회원 등급입니다: " + value));
    }
}