package org.example.fanzip.global.fcm;

import com.google.firebase.messaging.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class FcmService {

    private static final int CHUNK = 500; // 권장 최대치(500개까지 전송 가능)

    /** 단일 토큰 발송 */
    public String sendToToken(String token, String title, String body, String targetUrl) throws Exception {
        System.out.println("🚀 FCM 전송 시도: token=" + hide(token) + ", title=" + title);
        WebpushConfig webpush = WebpushConfig.builder()
                .setNotification(WebpushNotification.builder()
                        .setTitle(title)
                        .setBody(body)
                        .build())
                .setFcmOptions(WebpushFcmOptions.withLink(
                        targetUrl == null ? "http://localhost:5173" : targetUrl
                ))
                .build();

        Message msg = Message.builder()
                .setToken(token)
                .setWebpushConfig(webpush)
                .putData("targetUrl", targetUrl == null ? "" : targetUrl)
                .build();

        String messageId = FirebaseMessaging.getInstance().send(msg, false);
        log.info("[FCM] sent messageId={}, token={}", messageId, hide(token));
        return messageId;
    }

    /** 다중 토큰 발송 (sendAll → sendEach 로 교체) */
    public BatchResult sendToTokens(List<String> tokens, String title, String body, String targetUrl) throws Exception {
        if (tokens == null || tokens.isEmpty()) return new BatchResult(0, new ArrayList<>());

        int success = 0;
        List<String> invalidTokens = new ArrayList<>();

        WebpushConfig webpush = WebpushConfig.builder()
                .setNotification(WebpushNotification.builder()
                        .setTitle(title)
                        .setBody(body)
                        .build())
                .setFcmOptions(WebpushFcmOptions.withLink(
                        targetUrl == null ? "http://localhost:5173" : targetUrl
                ))
                .build();

        for (int i = 0; i < tokens.size(); i += CHUNK) {
            List<String> slice = tokens.subList(i, Math.min(i + CHUNK, tokens.size()));
            List<Message> msgs = new ArrayList<>(slice.size());

            for (String t : slice) {
                Message msg = Message.builder()
                        .setToken(t)
                        .setWebpushConfig(webpush)
                        .putData("targetUrl", targetUrl == null ? "" : targetUrl)
                        .build();
                msgs.add(msg);
            }

            // ✅ 핵심 변경: sendAll(...) → sendEach(...)
            BatchResponse resp = FirebaseMessaging.getInstance().sendEach(msgs, false);

            success += resp.getSuccessCount();

            for (int idx = 0; idx < resp.getResponses().size(); idx++) {
                SendResponse r = resp.getResponses().get(idx);
                if (!r.isSuccessful()) {
                    String failedToken = slice.get(idx);
                    invalidTokens.add(failedToken);
                    log.warn("[FCM] fail token={}, error={}", hide(failedToken),
                            r.getException() != null ? r.getException().getMessage() : "unknown");
                }
            }
        }

        log.info("[FCM] batch done: success={}, failed={}", success, invalidTokens.size());
        return new BatchResult(success, invalidTokens);
    }

    /** 결과 DTO */
    public record BatchResult(int successCount, List<String> invalidTokens) {}

    private String hide(String token) {
        if (token == null || token.length() < 12) return "****";
        return token.substring(0, 6) + "..." + token.substring(token.length() - 6);
    }
}
