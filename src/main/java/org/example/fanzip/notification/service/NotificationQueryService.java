package org.example.fanzip.notification.service;

import lombok.RequiredArgsConstructor;
import org.example.fanzip.notification.mapper.NotificationQueryMapper;   // ✅ 추가
import org.example.fanzip.notification.mapper.NotificationTokenMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationQueryService implements NotificationQueryPort {

    private final NotificationTokenMapper tokenMapper;
    private final NotificationQueryMapper queryMapper;                 // ✅ 추가

    @Override
    public List<String> findSubscriberTokens(Long influencerId) {
        return tokenMapper.findTokensByInfluencerId(influencerId);
    }

    @Override
    public String findInfluencerName(Long influencerId) {              // ✅ 구현
        return queryMapper.findInfluencerNameById(influencerId);
    }
}
