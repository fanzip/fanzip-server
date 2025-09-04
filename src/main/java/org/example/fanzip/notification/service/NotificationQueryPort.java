package org.example.fanzip.notification.service;

import java.util.List;

public interface NotificationQueryPort {
    List<String> findSubscriberTokens(Long influencerId);
    String findInfluencerName(Long influencerId);
}
