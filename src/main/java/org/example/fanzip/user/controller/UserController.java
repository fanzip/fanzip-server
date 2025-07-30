package org.example.fanzip.user.controller;


import lombok.RequiredArgsConstructor;
import org.example.fanzip.auth.jwt.JwtProcessor;
import org.example.fanzip.user.dto.RegisterDTO;
import org.example.fanzip.user.dto.UserDTO;
import org.example.fanzip.user.service.UserService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final JwtProcessor jwtProcessor;

    //회원가입
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterDTO registerDTO) throws Exception{

        Long userId=userService.register(registerDTO);

        String accessToken=jwtProcessor.generateAccessToken(userId);
        String refreshToken=jwtProcessor.generateRefreshToken(userId);

        ResponseCookie refreshCookie=ResponseCookie.from("refresh-token",refreshToken)
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(7*24*60*60)//7일
                .sameSite("Lax")
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                .header("Authorization", "Bearer "+accessToken)
                .body("sign up success");
    }

    @GetMapping("")
    public ResponseEntity<?> getUser(HttpServletRequest request){
        Long userID=(Long)request.getAttribute("userId");

        UserDTO userDTO=userService.getUser(userID);

        return ResponseEntity.ok().body(userDTO);

    }
}
