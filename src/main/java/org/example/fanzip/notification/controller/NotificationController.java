package org.example.fanzip.notification.controller;

import io.swagger.annotations.*;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.example.fanzip.global.fcm.FcmService;
import org.example.fanzip.notification.service.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.example.fanzip.security.JwtProcessor;


import java.util.List;

@Api(tags = "Notification", description = "알림 및 FCM 푸시 알림 관리 API")
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

    @ApiOperation(value = "FCM 토큰 등록/업데이트", notes = "사용자의 FCM 토큰을 등록하거나 업데이트합니다. 푸시 알림을 받기 위해 필요합니다.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "FCM 토큰 등록/업데이트 성공"),
            @ApiResponse(code = 400, message = "잘못된 요청 데이터"),
            @ApiResponse(code = 401, message = "인증 필요"),
            @ApiResponse(code = 500, message = "서버 오류")
    })
    @PostMapping("/token")
    public ResponseEntity<?> upsertToken(
            @RequestHeader("Authorization") String authorization,
            @ApiParam(value = "FCM 디바이스 토큰", required = true)
            @RequestParam String token,
            @ApiParam(value = "디바이스 타입 (android, ios, web)", example = "android")
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

    @ApiOperation(value = "인플루언서 구독자에게 알림 전송", notes = "인플루언서의 구독자들에게 FCM 푸시 알림을 전송합니다.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "알림 전송 성공"),
            @ApiResponse(code = 400, message = "잘못된 요청 데이터"),
            @ApiResponse(code = 401, message = "인증 필요"),
            @ApiResponse(code = 404, message = "인플루언서를 찾을 수 없음"),
            @ApiResponse(code = 500, message = "서버 오류")
    })
    @PostMapping("/send")
    public ResponseEntity<?> sendToSubscribers(
            @ApiParam(value = "알림 전송 요청 데이터", required = true)
            @RequestBody org.example.fanzip.notification.dto.NotificationRequestDTO req) throws Exception {
        int sent = notificationService.sendToInfluencerSubscribers(req);
        return ResponseEntity.ok("{\"sent\":" + sent + "}");
    }


    @Data public static class SendOne { private String token; private String title; private String body; private String targetUrl; }
    @Data public static class SendMany { private List<String> tokens; private String title; private String body; private String targetUrl; }
}
