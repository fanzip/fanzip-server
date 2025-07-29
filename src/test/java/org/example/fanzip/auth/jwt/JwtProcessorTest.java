package org.example.fanzip.auth.jwt;

import lombok.extern.slf4j.Slf4j;
import org.example.fanzip.global.config.RootConfig;
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

//    @Test
//    void genereteToken() {
//        String jwt=jwtProcessor.generateToken(3L);
//        log.info("jwt:{}",jwt);
//        assertNotNull(jwt);
//    }

    @Test
    void getUserId() {
        String token= "eyJhbGciOiJIUzI1NiJ9.eyJpYXQiOjE3NTM0MTEwNTksImV4cCI6MTc1MzQxMjg1OSwidXNlcklkIjoxMH0._LG-mo9lVuN-rlKOOUcPPH9vc_OFkZpJW2MLKTM4DoA";
        Long userId=jwtProcessor.getUserId(token);
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