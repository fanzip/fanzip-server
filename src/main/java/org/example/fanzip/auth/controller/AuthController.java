package org.example.fanzip.auth.controller;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.fanzip.auth.dto.KakaoUserDTO;
import org.example.fanzip.auth.jwt.JwtProcessor;
import org.example.fanzip.auth.service.KakaoOAuthService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

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

    // 카카오 로그인
    @GetMapping("/oauth/kakao-login")
    public ResponseEntity<?> kakaoCallback(@RequestParam String code) throws Exception{
        System.out.println("code:"+code);
        KakaoUserDTO kakaoUser= kakaoOAuthService.login(code);

        if(kakaoUser.isRegistered()){//가입한 유저
            log.info("기존회원입니다.");
            String accessToken=jwtProcessor.generateAccessToken(kakaoUser.getUserId());
            String refreshToken=jwtProcessor.generateRefreshToken(kakaoUser.getUserId());

            ResponseCookie refreshCookie=ResponseCookie.from("refresh-token",refreshToken)
                    .httpOnly(true)
                    .secure(false)
                    .path("/")
                    .maxAge(7*24*60*60)
                    .sameSite("Lax")
                    .build();

            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                    .header("Authorization", "Bearer "+accessToken)
                    .body("login success");
        }else{//가입하지 않은 사용자
            log.info("가입하지 않은 사용자입니다");

            return ResponseEntity.status(202)
                    .body(kakaoUser);
        }

    }

    @PostMapping("/reissue")
    public ResponseEntity<?> reissue(@CookieValue("refresh-token") String refreshToken) throws Exception{
        if(!jwtProcessor.validateToken(refreshToken)){
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("Refresh token expired or invalid");
        }

        Long userId = jwtProcessor.getUserId(refreshToken);
        String newAccessToken=jwtProcessor.generateAccessToken(userId);

        return ResponseEntity.ok()
                .header("Authorization", "Bearer "+newAccessToken)
                .body("Access token reissued");
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(){

        ResponseCookie deleteCookie=ResponseCookie.from("refresh-token", "")
                .httpOnly(true)
                .path("/")
                .maxAge(0)
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, deleteCookie.toString())
                .body("logout success");

    }
}
