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
        String jwt=jwtProcessor.generateToken(3L);
        log.info("jwt:{}",jwt);
        assertNotNull(jwt);
    }

    @Test
    void getUserId() {
        String token= "eyJhbGciOiJIUzI1NiJ9.eyJ1c2VySWQiOjksImlhdCI6MTc1MzMzMDQ1NSwiZXhwIjoxNzUzMzMyMjU1fQ.xrCrKujMwXBNo9Fk3C3j8KxBxWMoA6prkomxcTI3Qr4";
        String userId=jwtProcessor.getUserId(token);
        log.info("userId:{}",userId);
        assertNotNull(userId);
    }

    @Test
    void validateToken() {
        String token="eyJhbGciOiJIUzM4NCJ9.eyJzdWIiOiJ1c2VyMCIsImlhdCI6MTc1MzA3ODg1OCwiZXhwIjoxNzUzMDc5MTU4fQ.K1OUzZ6tk2g8bs8A6S_XtIQ8SASH_ZMy1Nk-YyxbiVHMzqLlQGhf3J7fKYjHGV-9";
        boolean isValid=jwtProcessor.validateToken(token);
        assertTrue(isValid);
    }
}