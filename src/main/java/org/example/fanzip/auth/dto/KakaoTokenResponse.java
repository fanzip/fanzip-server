package org.example.fanzip.auth.dto;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class KakaoTokenResponse {
    private String accessToken;
    private String refreshToken;
    private int expiresIn;
    private int refreshTokenExpiresIn;
}
