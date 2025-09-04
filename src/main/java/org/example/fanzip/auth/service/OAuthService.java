package org.example.fanzip.auth.service;

import org.example.fanzip.auth.dto.KakaoUserDTO;

import java.io.IOException;

public interface OAuthService {
    KakaoUserDTO login(String code) throws Exception;
}
