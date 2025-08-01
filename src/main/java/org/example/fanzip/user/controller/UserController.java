package org.example.fanzip.user.controller;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;
    private final JwtProcessor jwtProcessor;

    //회원가입
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterDTO registerDTO, HttpServletResponse response) throws Exception{
        Long userId=userService.register(registerDTO);

        String accessToken=jwtProcessor.generateAccessToken(userId);
        String refreshToken=jwtProcessor.generateRefreshToken(userId);

        int cookieAge=jwtProcessor.getRefreshTokenExpiryInSeconds();
        CookieUtil.addHttpOnlyCookie(response, "refresh-token", refreshToken, cookieAge);

        return ResponseEntity.ok()
                .header("Authorization", "Bearer "+accessToken)
                .body(Map.of("message","register success"));

    }

    @GetMapping("")
    public ResponseEntity<?> getUser(@AuthenticationPrincipal CustomUserPrincipal principal){
        log.info("=======getUser 함수 진입==========");
        Long userID=principal.getUserId();
        log.info("userID:{}",userID);
        UserDTO userDTO=userService.getUser(userID);

        return ResponseEntity.ok().body(userDTO);
    }
}
