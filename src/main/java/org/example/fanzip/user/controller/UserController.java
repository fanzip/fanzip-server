package org.example.fanzip.user.controller;


import lombok.RequiredArgsConstructor;
import org.example.fanzip.auth.jwt.JwtProcessor;
import org.example.fanzip.user.dto.AdditionalInfoDTO;
import org.example.fanzip.user.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final JwtProcessor jwtProcessor;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody AdditionalInfoDTO additionalInfoDTO){
        Long userId=userService.register(additionalInfoDTO);
        System.out.println(userId);
        String jwt=jwtProcessor.generateToken(userId);
        return ResponseEntity.ok()
                .header("Authorization", "Bearer "+jwt)
                .body("sign up successful");
    }
}
