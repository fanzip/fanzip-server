package org.example.fanzip.notification.dto;

import lombok.Data;

@Data
public class NotificationRequestDTO {
    private Long influencerId;
    private String title;
    private String body;       // ← DB의 message 컬럼으로 복사 저장할 거라 이름을 body로 둠
    private String targetUrl;
}
