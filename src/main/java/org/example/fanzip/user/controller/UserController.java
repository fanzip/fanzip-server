package org.example.fanzip.user.controller;


import lombok.RequiredArgsConstructor;
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

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody AdditionalInfoDTO additionalInfoDTO){
        userService.register(additionalInfoDTO);
        return ResponseEntity.ok("sign up success!!");
    }
}
