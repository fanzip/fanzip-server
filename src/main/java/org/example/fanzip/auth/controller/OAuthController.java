package org.example.fanzip.auth.controller;


import lombok.RequiredArgsConstructor;
import org.example.fanzip.auth.dto.KakaoUserDTO;
import org.example.fanzip.auth.service.KakaoOAuthService;
import org.example.fanzip.auth.jwt.JwtProcessor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/oauth")
@RequiredArgsConstructor
public class OAuthController {
    private final KakaoOAuthService kakaoOAuthService;
    private final JwtProcessor jwtProcessor;

//    프론트에서 인가코드 전달 (GET /oauth/kakao/callback?code=xxx)
    @GetMapping("/kakao/callback")
    public ResponseEntity<?> kakaoCallback(@RequestParam String code) throws Exception{
        System.out.println("code:"+code);
        KakaoUserDTO kakaoUser= kakaoOAuthService.login(code);
        String jwt=jwtProcessor.genereteToken(kakaoUser.getSocialType(),kakaoUser.getSocialId());

        if(kakaoUser.isRegistered()){//가입한 유저
            return ResponseEntity.ok().
                    header("Authorization", "Bearer "+jwt)
                    .body(kakaoUser);
        }else{//가입하지 않은 사용자
            return ResponseEntity.status(202)
                    .header("Authorization", "Bearer "+jwt)
                    .body(kakaoUser);
        }
    }
}
