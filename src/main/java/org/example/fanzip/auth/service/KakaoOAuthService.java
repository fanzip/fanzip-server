package org.example.fanzip.auth.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.example.fanzip.auth.dto.KakaoTokenResponse;
import org.example.fanzip.auth.dto.KakaoUserDTO;
import org.example.fanzip.user.dto.UserDTO;
import org.example.fanzip.user.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class KakaoOAuthService implements OAuthService {

    private final UserService userService;
    private final ObjectMapper objectMapper;

    @Value("${kakao.client-id}")
    private String clientId;

    @Value("${kakao.redirect-uri}")
    private String redirectUri;


    @Override
    public KakaoUserDTO login(String code) throws Exception {
        KakaoTokenResponse info=requestAccessToken(code);
        JsonNode profile=requestUserInfo(info.getAccessToken());

        String socialType="kakao";
        String socialId=profile.get("id").asText();

        JsonNode kakaoAccountNode=profile.path("kakao_account");
        String email=kakaoAccountNode.get("email").asText(null);
        System.out.println("email:"+email);

        UserDTO user=userService.findBySocialTypeAndSocialId(socialType,socialId);

        boolean isRegistered=(user!=null);

        return KakaoUserDTO.builder()
                .socialType(socialType)
                .socialId(socialId)
                .email(email)
                .isRegistered(isRegistered)
                .build();
    }
//    private KakaoTokenResponse refreshAccessToken(String socialId) throws IOException {
//        String tokenUrl="https://kauth.kakao.com/oauth/token";
//
//        String refreshToken=userService.getRefreshtoken("kakao", socialId);
//        if(refreshToken==null){throw new IllegalStateException("No refresh token found");}
//
//        RestTemplate restTemplate = new RestTemplate();
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
//
//        MultiValueMap<String, String> params=new LinkedMultiValueMap<>();
//        params.add("grant_type","refresh_token");
//        params.add("client_id",clientId);
//        params.add("refresh_token",refreshToken);
//
//        HttpEntity<MultiValueMap<String, String>> request=new HttpEntity<>(params,headers);
//        ResponseEntity<String> response=restTemplate.postForEntity(tokenUrl,request,String.class);
//
//        JsonNode node=objectMapper.readTree(response.getBody());
//        String newAccessToken=node.get("access_token").asText();
//
//        if(node.has("refresh_token")){
//            String newRefreshToken=node.get("refresh_token").asText();
//            userService.updateRefreshToken("kakao", newRefreshToken);
//        }
//        return newAccessToken;
//    }

    private KakaoTokenResponse requestAccessToken(String code) throws IOException {
        String tokenUrl="https://kauth.kakao.com/oauth/token";

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params=new LinkedMultiValueMap<>();
        params.add("grant_type","authorization_code");
        params.add("client_id",clientId);
        params.add("redirect_uri",redirectUri);
        params.add("code",code);

        HttpEntity<MultiValueMap<String, String>> request=new HttpEntity<>(params,headers);
        ResponseEntity<String> response=restTemplate.postForEntity(tokenUrl,request,String.class);

        JsonNode node=objectMapper.readTree(response.getBody());
        System.out.println("카카오 응답 JSON: " + node.toPrettyString());

        return new KakaoTokenResponse(
                node.get("access_token").asText(),
                node.has("refresh_token")?node.get("refresh_token").asText():null,
                node.get("expires_in").asInt(),
                node.has("refresh_token_expires_in")?node.get("refresh_token_expires_in").asInt():0
        );
    }

    private JsonNode requestUserInfo(String accessToken) throws IOException {
        String url="https://kapi.kakao.com/v2/user/me";

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        HttpEntity<Void> request=new HttpEntity<>(headers);
        ResponseEntity<String> response=restTemplate.exchange(
            url, HttpMethod.GET,request,String.class
        );

        JsonNode node=objectMapper.readTree(response.getBody());
        System.out.println("node: " + node.toPrettyString());
        return node;
    }
}
