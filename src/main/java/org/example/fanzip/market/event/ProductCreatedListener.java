package org.example.fanzip.market.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.fanzip.notification.dto.NotificationRequestDTO;
import org.example.fanzip.notification.mapper.PushTokenMapper;
import org.example.fanzip.notification.service.NotificationService; // 프로젝트 경로 맞추기
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.transaction.event.TransactionPhase;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductCreatedListener {

    private final PushTokenMapper pushTokenMapper;          // 토큰 조회
    private final NotificationService notificationService;  // FCM 발송

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void on(ProductCreatedEvent e) {
        // ACTIVE 구독자 토큰 조회
        List<String> tokens = pushTokenMapper.findTokensByInfluencer(e.getInfluencerId(), "ACTIVE",  "WEB");
        if (tokens == null || tokens.isEmpty()) {
            log.info("[Market][Notify] no tokens: influencerId={}, productId={}", e.getInfluencerId(), e.getProductId());
            return;
        }

        // 알림 내용 + 딥링크
        String title = "새 공구가 올라왔어요";
        String body  = "「" + e.getProductName() + "」 지금 확인해보세요!";
        String clickPath = "/market/" + e.getProductId();

        // NotificationService에서 요구하는 DTO로 위임
        NotificationRequestDTO req = new NotificationRequestDTO();
        req.setInfluencerId(e.getInfluencerId());
        req.setTitle(title);
        req.setBody(body);
        req.setTargetUrl(clickPath);
        // (DTO에 imageUrl 필드가 있다면) req.setImageUrl(e.getThumbnailImage());

        try {
            int sent = notificationService.sendToInfluencerSubscribers(req);
            log.info("[Market][Notify] sent={} influencerId={} productId={}", sent, e.getInfluencerId(), e.getProductId());
        } catch (Exception ex) {
            log.error("[Market][Notify] send failed influencerId={} productId={}", e.getInfluencerId(), e.getProductId(), ex);
        }
    }
}
