package org.example.fanzip.user.controller;

import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.fanzip.global.metric.BusinessMetricsService;
import org.example.fanzip.security.CookieUtil;
import org.example.fanzip.security.CustomUserPrincipal;
import org.example.fanzip.security.JwtProcessor;
import org.example.fanzip.user.dto.RegisterDTO;
import org.example.fanzip.user.dto.UserDTO;
import org.example.fanzip.user.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@Api(tags = "User", description = "사용자 관리 API")
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;
    private final JwtProcessor jwtProcessor;
    private final BusinessMetricsService businessMetricsService;
    @ApiOperation(value = "사용자 회원가입", notes = "새로운 사용자를 등록하고 인증 토큰을 발급합니다.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "회원가입 성공", response = Map.class),
            @ApiResponse(code = 400, message = "잘못된 요청 데이터 또는 중복된 이메일"),
            @ApiResponse(code = 500, message = "서버 오류")
    })
    @PostMapping("/register")
    public ResponseEntity<?> register(
            @ApiParam(value = "회원가입 요청 정보", required = true)
            @RequestBody RegisterDTO registerDTO, 
            HttpServletResponse response) throws Exception{
        try{
            UserDTO userDTO=userService.register(registerDTO);

            String accessToken=jwtProcessor.generateAccessToken(userDTO.getUserId(), String.valueOf(userDTO.getRole()));
            String refreshToken=jwtProcessor.generateRefreshToken(userDTO.getUserId(), String.valueOf(userDTO.getRole()));


            int cookieAge=jwtProcessor.getRefreshTokenExpiryInSeconds();
            CookieUtil.addHttpOnlyCookie(response, "refresh-token", refreshToken, cookieAge);

            businessMetricsService.recordUserRegistration();
            log.info("새 사용자 등록 완료: userId={}", userDTO.getUserId());

            return ResponseEntity.ok()
                    .header("Authorization", "Bearer "+accessToken)
                    .body(Map.of("message","register success"));
        }catch (Exception e){
            businessMetricsService.recordRegistrationFailure(e.getClass().getSimpleName());
            log.error("회원가입 실패", e);
            throw e;
        }


    }

    @ApiOperation(value = "사용자 정보 조회", notes = "현재 로그인된 사용자의 정보를 조회합니다.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "사용자 정보 조회 성공", response = UserDTO.class),
            @ApiResponse(code = 401, message = "인증 필요"),
            @ApiResponse(code = 404, message = "사용자를 찾을 수 없음"),
            @ApiResponse(code = 500, message = "서버 오류")
    })
    @GetMapping("")
    public ResponseEntity<?> getUser(
            @AuthenticationPrincipal CustomUserPrincipal principal){
        log.info("=======getUser 함수 진입==========");
        Long userID=principal.getUserId();
        log.info("userID:{}",userID);
        UserDTO userDTO=userService.getUser(userID);

        return ResponseEntity.ok().body(userDTO);
    }
}
