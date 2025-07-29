package org.example.fanzip.user.mapper;

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
class UserMapperTest {

    @Autowired
    private UserMapper userMapper;

    @Test
    void findBySocialTypeAndSocialId() {
        UserDTO userDTO = userMapper.findBySocialTypeAndSocialId("kakao", "12345");
        log.info("userDTO={}", userDTO);
        assertNotNull(userDTO);
    }

    @Test
    void insertUser() {
        UserDTO userDTO = UserDTO.builder()
                .socialType("kakao")
                .socialId("123456")
                .created_at(new Date())
                .build();
        userMapper.insertUser(userDTO);

    }

    @Test
    void updateAdditionalInfo() {
//        AdditionalInfoDTO additionalInfoDTO = AdditionalInfoDTO.builder()
//                .name("HongGilDong")
//                .phone("010-1234-5678")
//                .email("HongGilDong@gmail.com")
//                .build();
//        userMapper.updateAdditionalInfo("kakao", "12345",additionalInfoDTO);
//        UserDTO userDTO = userMapper.findBySocialTypeAndSocialId("kakao", "12345");
//        assertNotNull(userDTO);
    }
}