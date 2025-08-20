package org.example.fanzip.meeting.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.fanzip.notification.dto.NotificationRequestDTO;
import org.example.fanzip.notification.service.NotificationService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.transaction.event.TransactionPhase;

@Slf4j
@RequiredArgsConstructor
@Component
public class FanMeetingCreatedListener {

    private final NotificationService notificationService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onCreated(FanMeetingCreatedEvent e) {
        // 알림 타이틀/본문/딥링크 구성
        String title = "새 팬미팅이 등록됐어요 🎉";
        String body  = "‘" + e.title() + "’";
        String clickPath = "/reservation/" + e.meetingId();

        // DTO 구성
        NotificationRequestDTO req = new NotificationRequestDTO();
        req.setInfluencerId(e.influencerId());
        req.setTitle(title);
        req.setBody(body);
        req.setTargetUrl(clickPath);
        // 필요 시 썸네일 등 추가 필드 설정 가능
        // req.setImageUrl(e.getThumbnailUrl());

        try {
            int sent = notificationService.sendToInfluencerSubscribers(req);
            log.info("[Meeting][Notify] sent={} influencerId={} meetingId={}",
                    sent, e.influencerId(), e.meetingId());
        } catch (Exception ex) {
            log.error("[Meeting][Notify] send failed influencerId={} meetingId={}",
                    e.influencerId(), e.meetingId(), ex);
        }
    }
}
