package org.example.fanzip.notification.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;   // 👈 추가
import org.example.fanzip.global.fcm.FcmService;
import org.example.fanzip.notification.domain.NotificationVO;
import org.example.fanzip.notification.dto.NotificationRequestDTO;
import org.example.fanzip.notification.mapper.NotificationMapper;
import org.example.fanzip.notification.mapper.PushTokenMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j  // 👈 추가
@Service
@RequiredArgsConstructor
public class NotificationService {

    private static final String MEMBERSHIP_ACTIVE = "ACTIVE";
    private static final String DEVICE_TYPE_WEB = "WEB";

    private final PushTokenMapper pushTokenMapper;
    private final NotificationMapper notificationMapper;
    private final FcmService fcmService;

    @Transactional
    public void upsertToken(Long userId, String token, String deviceType) {
        log.info("🔍 [TOKEN] upsertToken 시작: userId={}, token={}, deviceType={}",
                userId, token.substring(0, Math.min(token.length(), 20)) + "...", deviceType);

        // 0) 토큰 없으면 스킵 (안전 가드)
        if (token == null || token.isBlank()) {
            log.warn("⚠️ [TOKEN] 토큰이 null 또는 빈 문자열");
            return;
        }

        // 1) 디바이스 타입 통일 (소문자/널 모두 "WEB"으로)
        String dt = (deviceType == null || deviceType.isBlank())
                ? "WEB"
                : deviceType.toUpperCase();
        log.info("🔍 [TOKEN] 정규화된 deviceType: {}", dt);

        try {
            // 2) 같은 토큰을 가진 레코드 먼저 삭제 (UNIQUE 제약조건 충돌 방지)
            int deletedByToken = pushTokenMapper.deleteByToken(token);
            log.info("✅ [TOKEN] 동일 토큰 삭제: {} rows", deletedByToken);

            // 3) 같은 유저-디바이스 기존 행 제거 (uq_user_device 충돌 예방)
            int deletedByUserDevice = pushTokenMapper.deleteByUserAndDevice(userId, dt);
            log.info("✅ [TOKEN] 유저-디바이스 토큰 삭제: {} rows", deletedByUserDevice);

            // 4) 업서트 (이제 충돌 없음)
            int affectedRows = pushTokenMapper.insertOrUpdateByToken(userId, token, dt);
            log.info("✅ [TOKEN] 토큰 업서트 완료: {} rows affected", affectedRows);

            if (affectedRows == 0) {
                log.error("❌ [TOKEN] 업서트 실패: 영향받은 행이 0개");
                throw new RuntimeException("토큰 등록 실패: 영향받은 행이 0개");
            }

            log.info("🎉 [TOKEN] 토큰 등록 성공");

        } catch (Exception e) {
            log.error("❌ [TOKEN] 토큰 등록 중 예외 발생", e);
            throw new RuntimeException("토큰 등록 실패: " + e.getMessage(), e);
        }
    }
    /** 구독(활성) 중인 사용자에게만 일괄 발송 + 로그 저장 + 실패 토큰 정리 */
    @Transactional(rollbackFor = Exception.class) // 실패 시 로그 insert도 롤백
    public int sendToInfluencerSubscribers(NotificationRequestDTO req) throws Exception {
        log.info("[NOTI] send start: influencerId={}, title={}, url={}",
                req.getInfluencerId(), req.getTitle(), req.getTargetUrl());

        // 1) 발송 로그 저장
        NotificationVO vo = new NotificationVO();
        vo.setInfluencerId(req.getInfluencerId());
        vo.setTitle(req.getTitle());
        vo.setMessage(req.getBody());     // DTO.body → DB notifications.message
        vo.setTargetUrl(req.getTargetUrl());
        notificationMapper.insert(vo);
        log.info("[NOTI] insert log done: notificationId={}", vo.getNotificationId());

        // 2) 구독자 토큰 조회 (status=ACTIVE 필터)
        List<String> tokens =
                pushTokenMapper.findTokensByInfluencer(req.getInfluencerId(), MEMBERSHIP_ACTIVE, DEVICE_TYPE_WEB);
        log.info("[NOTI] tokens fetched: size={}", (tokens == null ? 0 : tokens.size()));
        if (tokens == null || tokens.isEmpty()) return 0;

        // 3) 발송
        try {
            var result = fcmService.sendToTokens(tokens, req.getTitle(), req.getBody(), req.getTargetUrl());
            log.info("[NOTI] FCM sent: success={}, invalid={}",
                    result.successCount(), result.invalidTokens().size());

            // 4) 실패 토큰 정리
            if (!result.invalidTokens().isEmpty()) {
                int del = pushTokenMapper.deleteTokens(result.invalidTokens());
                log.info("[NOTI] invalid tokens deleted: {}", del);
            }
            return result.successCount();
        } catch (Exception e) {
            log.error("[NOTI] FCM send failed", e); // 예외 전체 스택 찍힘
            throw e; // 롤백
        }
    }
}
