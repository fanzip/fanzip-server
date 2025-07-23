package org.example.fanzip.auth.jwt;

import lombok.extern.slf4j.Slf4j;
import org.example.fanzip.config.RootConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {RootConfig.class})
@Slf4j
class JwtProcessorTest {

    @Autowired
    JwtProcessor jwtProcessor;

    @Test
    void genereteToken() {
        String socialType="kakao";
        String socialId="1234";
        String jwt=jwtProcessor.genereteToken(socialType,socialId);
        log.info("jwt:{}",jwt);
        assertNotNull(jwt);
//        String token= jwtProcessor.genereteToken(username);
//        log.info(token);
//        assertNotNull(token);
    }

    @Test
    void getUsername() {
        String token="eyJhbGciOiJIUzM4NCJ9.eyJzdWIiOiJrYWthbzoxMjM0IiwiaWF0IjoxNzUzMTY5NTEwLCJleHAiOjE3NTMxNjk4MTB9.KXplQCNqaK6r4UbAfpdTd9clTRE6T3HCeo8BdsRQ3cNsO1joHky5o8uQIxmdThe6";
        String username=jwtProcessor.getUsername(token);
        log.info(username);
        assertNotNull(username);
    }

    @Test
    void validateToken() {
        String token="eyJhbGciOiJIUzM4NCJ9.eyJzdWIiOiJ1c2VyMCIsImlhdCI6MTc1MzA3ODg1OCwiZXhwIjoxNzUzMDc5MTU4fQ.K1OUzZ6tk2g8bs8A6S_XtIQ8SASH_ZMy1Nk-YyxbiVHMzqLlQGhf3J7fKYjHGV-9";

        boolean isValid=jwtProcessor.validateToken(token);
//        log.info(isValid);
        assertTrue(isValid);
    }
}