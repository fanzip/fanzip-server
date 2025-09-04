package org.example.fanzip.global.fcm;

import com.google.firebase.messaging.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class FcmService {

    private static final int CHUNK = 500; // 권장 최대치(500개까지 전송 가능)

    /** 단일 토큰 발송 - DATA ONLY */
    public String sendToToken(String token, String title, String body, String targetUrl) throws Exception {
        System.out.println("🚀 FCM 전송 시도(data-only): token=" + hide(token) + ", title=" + title);

        // (선택) Webpush 헤더/옵션 - notification은 넣지 않음!
        WebpushConfig webpush = WebpushConfig.builder()
                .putHeader("Urgency", "high") // 빠른 알림
                // setFcmOptions(link)는 notification 메시지에서 주로 동작하므로 data-only에선 필요 없음
                .build();

        Message msg = Message.builder()
                .setToken(token)
                .setWebpushConfig(webpush)
                .putAllData(buildData(title, body, targetUrl))
                .build();

        String messageId = FirebaseMessaging.getInstance().send(msg, false);
        log.info("[FCM] sent (data-only) messageId={}, token={}", messageId, hide(token));
        return messageId;
    }

    /** 다중 토큰 발송 - DATA ONLY (sendEach) */
    public BatchResult sendToTokens(List<String> tokens, String title, String body, String targetUrl) throws Exception {
        if (tokens == null || tokens.isEmpty()) return new BatchResult(0, new ArrayList<>());

        int success = 0;
        List<String> invalidTokens = new ArrayList<>();

        WebpushConfig webpush = WebpushConfig.builder()
                .putHeader("Urgency", "high")
                .build();

        for (int i = 0; i < tokens.size(); i += CHUNK) {
            List<String> slice = tokens.subList(i, Math.min(i + CHUNK, tokens.size()));
            List<Message> msgs = new ArrayList<>(slice.size());

            for (String t : slice) {
                Message msg = Message.builder()
                        .setToken(t)
                        .setWebpushConfig(webpush)
                        .putAllData(buildData(title, body, targetUrl))
                        .build();
                msgs.add(msg);
            }

            BatchResponse resp = FirebaseMessaging.getInstance().sendEach(msgs, false);
            success += resp.getSuccessCount();

            for (int idx = 0; idx < resp.getResponses().size(); idx++) {
                SendResponse r = resp.getResponses().get(idx);
                if (!r.isSuccessful()) {
                    String failedToken = slice.get(idx);
                    invalidTokens.add(failedToken);
                    String err = (r.getException() != null) ? r.getException().getMessage() : "unknown";
                    log.warn("[FCM] fail token={}, error={}", hide(failedToken), err);
                }
            }
        }

        log.info("[FCM] batch done(data-only): success={}, failed={}", success, invalidTokens.size());
        return new BatchResult(success, invalidTokens);
    }

    /** 결과 DTO */
    public record BatchResult(int successCount, List<String> invalidTokens) {}

    /** data-only 페이로드 구성 */
    private Map<String, String> buildData(String title, String body, String targetUrl) {
        String url = (targetUrl == null || targetUrl.isBlank()) ? "/" : targetUrl;
        return Map.of(
                "title", (title == null ? "알림" : title),
                "body", (body == null ? "" : body),
                "targetUrl", url
        );
    }

    private String hide(String token) {
        if (token == null || token.length() < 12) return "****";
        return token.substring(0, 6) + "..." + token.substring(token.length() - 6);
    }
}
