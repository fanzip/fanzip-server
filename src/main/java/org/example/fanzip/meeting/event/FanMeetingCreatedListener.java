package org.example.fanzip.meeting.event;

import lombok.RequiredArgsConstructor;
import org.example.fanzip.global.fcm.FcmService;
import org.example.fanzip.influencer.service.InfluencerService;
import org.example.fanzip.notification.service.NotificationQueryPort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.List;

@RequiredArgsConstructor
@Component
public class FanMeetingCreatedListener {

    private final NotificationQueryPort notificationQueryPort;
    private final FcmService fcmService;
    private final InfluencerService influencerService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onCreated(FanMeetingCreatedEvent e) {
        // 1) 구독자 토큰 조회
        List<String> tokens = notificationQueryPort.findSubscriberTokens(e.influencerId());
        if (tokens == null || tokens.isEmpty()) return;

        // 2) 인플루언서 이름 조회 (fallback)
        String influencerName = notificationQueryPort.findInfluencerName(e.influencerId());
        if (influencerName == null || influencerName.isBlank()) influencerName = "인플루언서";

        // 3) 푸시 메시지 (⛔ 시간 문구 제거)
        String title = influencerName + "님의 새 팬미팅이 등록됐어요 🎉";
        String body  = "‘" + e.title() + "’"; // 시간 표기 X

        // 4) 이동 경로 (프로젝트 규칙에 맞춰 선택)
        //    - FcmService에 toFullUrl 보정 로직이 있다면 상대경로만 넘겨도 됨
        String targetUrl = "/reservation/" + e.meetingId();
        // String targetUrl = "/fanmeetings/" + e.meetingId(); // 이 경로가 맞다면 위 대신 이걸 사용

        try {
            fcmService.sendToTokens(tokens, title, body, targetUrl);
        } catch (Exception ex) {
            System.err.println("[FCM] 팬미팅 알림 전송 실패: " + ex.getMessage());
        }
    }
}
