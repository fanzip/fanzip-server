package org.example.fanzip.notification.domain;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class NotificationVO {
    private Long notificationId;
    private Long influencerId;
    private String title;
    private String message;
    private String targetUrl;
    private LocalDateTime createdAt;
}

