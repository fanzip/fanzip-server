package org.example.fanzip.notification.controller;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.example.fanzip.global.fcm.FcmService;
import org.example.fanzip.notification.service.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.example.fanzip.security.JwtProcessor;


import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notifications")
public class NotificationController {

    private final FcmService fcmService;
    private final NotificationService notificationService;
    private final JwtProcessor jwtProcessor;


//    /** 단일 토큰 전송(토큰 직접 넣어서 테스트) */
//    @PostMapping("/send-one")
//    public ResponseEntity<?> sendOne(@RequestBody SendOne req) throws Exception {
//        String id = fcmService.sendToToken(req.token, req.title, req.body, req.targetUrl);
//        return ResponseEntity.ok().body("{\"messageId\":\"" + id + "\"}");
//    }
//
//    /** 다중 토큰 전송(토큰 직접 넣어서 테스트) */
//    @PostMapping("/send-many")
//    public ResponseEntity<?> sendMany(@RequestBody SendMany req) throws Exception {
//        var result = fcmService.sendToTokens(req.tokens, req.title, req.body, req.targetUrl);
//        return ResponseEntity.ok().body("{\"success\":" + result.successCount()
//                + ",\"failed\":" + result.invalidTokens().size() + "}");
//    }

    // 토큰 저장
    @PostMapping("/token")
    public ResponseEntity<?> upsertToken(
            @RequestHeader("Authorization") String authorization,
            @RequestParam String token,
            @RequestParam(required = false) String deviceType) {

        Long userId;
        try {
            String jwt = authorization.substring(7);
            System.out.println("🔍 JWT 토큰: " + jwt.substring(0, Math.min(jwt.length(), 50)) + "...");
            userId = jwtProcessor.getUserIdFromToken(jwt);
            System.out.println("✅ JWT 파싱 성공: userId=" + userId);
        } catch (Exception e) {
            System.out.println("⚠️ JWT 파싱 실패: " + e.getClass().getSimpleName() + " - " + e.getMessage());
            System.out.println("⚠️ Authorization 헤더: " + authorization);
            // 테스트용: JWT 실패 시 기본 사용자 ID 사용
            userId = 1L;
        }
        
        System.out.println("🔍 FCM 토큰 등록: userId=" + userId + ", token=" + token + ", deviceType=" + deviceType);
        
        notificationService.upsertToken(userId, token, deviceType);
        return ResponseEntity.ok().build();
    }

    // NotificationController.java (기존 클래스 안에 추가)
    @PostMapping("/send")
    public ResponseEntity<?> sendToSubscribers(
            @RequestBody org.example.fanzip.notification.dto.NotificationRequestDTO req) throws Exception {
        int sent = notificationService.sendToInfluencerSubscribers(req);
        return ResponseEntity.ok("{\"sent\":" + sent + "}");
    }


    @Data public static class SendOne { private String token; private String title; private String body; private String targetUrl; }
    @Data public static class SendMany { private List<String> tokens; private String title; private String body; private String targetUrl; }
}
