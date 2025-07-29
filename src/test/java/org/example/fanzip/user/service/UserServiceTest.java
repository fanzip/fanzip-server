package org.example.fanzip.user.service;

import lombok.extern.slf4j.Slf4j;
import org.example.fanzip.global.config.RootConfig;
//import org.example.fanzip.user.dto.AdditionalInfoDTO;
import org.example.fanzip.user.dto.UserDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;


@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {RootConfig.class})
@Slf4j
class UserServiceTest {

    @Autowired
    private UserService service;

    @Test
    void findBySocialTypeAndSocialId() {
        UserDTO userDTO = service.findBySocialTypeAndSocialId("kakao", "1234567");
        log.info("userDTO={}", userDTO);
        assertNotNull(userDTO);
    }

    @Test
    void registerUser() {
//        String socialType="kakao";
//        String socialId="123456";
//        service.loginOrRegister(socialType,socialId,null);

    }

    @Test
    void updateAdditionalInfo() {
//        AdditionalInfoDTO additionalInfoDTO = AdditionalInfoDTO.builder()
//                .name("yonseeee")
//                .phone("010-1234-5678")
//                .email("yonseee@gmail.com")
//                .build();
//        service.updateAdditionalInfo("kakao","1234567", additionalInfoDTO);
    }
}