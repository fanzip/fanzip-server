package org.example.fanzip.auth.controller;


import io.micrometer.core.instrument.Timer;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
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

@Api(tags = "인증 관리", description = "카카오 OAuth 로그인 및 JWT 토큰 관리 API")
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

    @ApiOperation(value = "카카오 로그인 URL 생성", notes = "카카오 OAuth 로그인을 위한 URL을 생성합니다.")
    @ApiResponses({
        @ApiResponse(code = 200, message = "카카오 로그인 URL 생성 성공"),
        @ApiResponse(code = 500, message = "서버 오류")
    })
    @GetMapping("/oauth/kakao-url")
    public ResponseEntity<String> getKakaoLoginUrl(){
        String url = "https://kauth.kakao.com/oauth/authorize"
                +"?response_type=code"
                +"&client_id="+clientId
                +"&redirect_uri="+redirectUri;
        return ResponseEntity.ok(url);
    }

    @ApiOperation(value = "카카오 로그인 콜백", notes = "카카오 OAuth 인증 후 콜백을 처리합니다. 기존 회원은 로그인, 신규 회원은 회원가입 유도")
    @ApiResponses({
        @ApiResponse(code = 200, message = "기존 회원 로그인 성공 - Authorization 헤더에 Access Token 포함"),
        @ApiResponse(code = 202, message = "신규 회원 - 회원가입 필요, 카카오 사용자 정보 반환"),
        @ApiResponse(code = 400, message = "잘못된 인증 코드"),
        @ApiResponse(code = 500, message = "OAuth 처리 오류")
    })
    @GetMapping("/oauth/kakao-login")
    public ResponseEntity<?> kakaoCallback(
            @ApiParam(value = "카카오 인증 코드", required = true) @RequestParam String code, 
            HttpServletResponse response) throws Exception{

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

    @ApiOperation(value = "Access Token 재발급", notes = "Refresh Token을 사용하여 새로운 Access Token을 발급합니다.")
    @ApiResponses({
        @ApiResponse(code = 200, message = "토큰 재발급 성공 - Authorization 헤더에 새 Access Token 포함"),
        @ApiResponse(code = 401, message = "Refresh Token이 만료되었거나 유효하지 않음"),
        @ApiResponse(code = 400, message = "Refresh Token이 쿠키에 없음")
    })
    @PostMapping("/reissue")
    public ResponseEntity<?> reissue(
            @ApiParam(value = "HTTP-Only 쿠키의 Refresh Token", required = true) @CookieValue("refresh-token") String refreshToken, 
            HttpServletResponse response) throws Exception{
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

    @ApiOperation(value = "로그아웃", notes = "HTTP-Only 쿠키에서 Refresh Token을 제거하여 로그아웃 처리합니다.")
    @ApiResponses({
        @ApiResponse(code = 200, message = "로그아웃 성공"),
        @ApiResponse(code = 500, message = "서버 오류")
    })
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response){
        CookieUtil.removeCookie(response, "refresh-token");

        return ResponseEntity.ok(Map.of("message","logout success"));
    }
}
