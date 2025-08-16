package org.example.fanzip.auth.controller;


import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.fanzip.auth.dto.KakaoUserDTO;
import org.example.fanzip.auth.service.KakaoOAuthService;
import org.example.fanzip.global.metric.BusinessMetricsService;
import org.example.fanzip.security.CookieUtil;
import org.example.fanzip.security.JwtProcessor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {
    private final KakaoOAuthService kakaoOAuthService;
    private final JwtProcessor jwtProcessor;
    private final BusinessMetricsService businessMetricsService;

    @Value("${kakao.client-id}")
    private String clientId;

    @Value("${kakao.redirect-uri}")
    private String redirectUri;

    //  카카오 로그인 URL 생성
    @GetMapping("/oauth/kakao-url")
    public ResponseEntity<String> getKakaoLoginUrl(){
        String url = "https://kauth.kakao.com/oauth/authorize"
                +"?response_type=code"
                +"&client_id="+clientId
                +"&redirect_uri="+redirectUri;
        return ResponseEntity.ok(url);
    }

    // 카카오 로그인(기존 유저면 로그인 처리, 신규 유저면 회원가입 유도)
    @GetMapping("/oauth/kakao-login")
    public ResponseEntity<?> kakaoCallback(@RequestParam String code, HttpServletResponse response) throws Exception{

        Timer.Sample loginTimer= businessMetricsService.startLoginTimer();
        businessMetricsService.recordLoginAttempt();

        try{
            KakaoUserDTO kakaoUser= kakaoOAuthService.login(code);

            if(kakaoUser.isRegistered()){//가입한 유저
                log.info("기존회원입니다.");

                String accessToken=jwtProcessor.generateAccessToken(kakaoUser.getUserId(), String.valueOf(kakaoUser.getRole()));
                String refreshToken=jwtProcessor.generateRefreshToken(kakaoUser.getUserId(),  String.valueOf(kakaoUser.getRole()));

                int cookieAge=jwtProcessor.getRefreshTokenExpiryInSeconds();
                CookieUtil.addHttpOnlyCookie(response, "refresh-token", refreshToken, cookieAge);

                businessMetricsService.recordLoginDuration(loginTimer);

                return ResponseEntity.ok()
                        .header("Authorization", "Bearer "+accessToken)
                        .body(Map.of("message","login success"));
            }else{//가입하지 않은 사용자
                log.info("가입하지 않은 사용자입니다");

                businessMetricsService.recordLoginFailure("user_not_registered");
                businessMetricsService.recordLoginDuration(loginTimer);

                return ResponseEntity.status(202)
                        .body(kakaoUser);
            }
        }catch (Exception e){
            log.error("카카오 로그인 중 오류 발생", e);
            businessMetricsService.recordLoginFailure("oauth_error");
            businessMetricsService.recordLoginDuration(loginTimer);

            throw e;
        }


    }

    //    Access Token 재발급
    @PostMapping("/reissue")
    public ResponseEntity<?> reissue(@CookieValue("refresh-token") String refreshToken, HttpServletResponse response) throws Exception{
        log.info("=========reissue 진입=======");
        if(refreshToken==null||!jwtProcessor.validateToken(refreshToken)){
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message","Refresh token expired or invalid"));
        }

        Long userId = jwtProcessor.getUserIdFromToken(refreshToken);
        String role=jwtProcessor.getRoleFromToken(refreshToken);
        String newAccessToken=jwtProcessor.generateAccessToken(userId, role);
        String newRefreshToken = jwtProcessor.generateRefreshToken(userId, role);

        int cookieAge=jwtProcessor.getRefreshTokenExpiryInSeconds();
        CookieUtil.addHttpOnlyCookie(response, "refresh-token", newRefreshToken, cookieAge);

        return ResponseEntity.ok()
                .header("Authorization", "Bearer "+newAccessToken)
                .body(Map.of("message", "Access token reissued"));
    }

    //    로그아웃: refresh-token 제거
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response){
        CookieUtil.removeCookie(response, "refresh-token");

        return ResponseEntity.ok(Map.of("message","logout success"));
    }
}
