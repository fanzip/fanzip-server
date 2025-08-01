package org.example.fanzip.auth.controller;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.fanzip.auth.dto.KakaoUserDTO;
import org.example.fanzip.auth.service.KakaoOAuthService;
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
        log.info("kakaoCallback 함수 진입");
        log.info("카카오 인가코드: {}", code);

        KakaoUserDTO kakaoUser= kakaoOAuthService.login(code);

        if(kakaoUser.isRegistered()){//가입한 유저
            log.info("기존회원입니다.");

            String accessToken=jwtProcessor.generateAccessToken(kakaoUser.getUserId());
            String refreshToken=jwtProcessor.generateRefreshToken(kakaoUser.getUserId());

            int cookieAge=jwtProcessor.getRefreshTokenExpiryInSeconds();
            CookieUtil.addHttpOnlyCookie(response, "refresh-token", refreshToken, cookieAge);

            return ResponseEntity.ok()
                    .header("Authorization", "Bearer "+accessToken)
                    .body(Map.of("message","login success"));
        }else{//가입하지 않은 사용자
            log.info("가입하지 않은 사용자입니다");

            return ResponseEntity.status(202)
                    .body(kakaoUser);
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
        String newAccessToken=jwtProcessor.generateAccessToken(userId);
        String newRefreshToken = jwtProcessor.generateRefreshToken(userId);

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
