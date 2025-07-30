package org.example.fanzip.user.service;

import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.example.fanzip.user.dto.RegisterDTO;
import org.example.fanzip.user.dto.UserDTO;
import org.example.fanzip.user.mapper.UserMapper;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserMapper mapper;

    public UserDTO findBySocialTypeAndSocialId(String socialType, String socialId){
        return mapper.findBySocialTypeAndSocialId(socialType, socialId);
    }
    public Long register(RegisterDTO dto){
        UserDTO newUser=UserDTO.builder()
                .socialType(dto.getSocialType())
                .socialId(dto.getSocialId())
                .name(dto.getName())
                .phone(dto.getPhone())
                .email(dto.getEmail())
                .build();

        mapper.insertUser(newUser);
        return newUser.getUserId();
    }
    public UserDTO getUser(@Param("userId") Long userId){
        return mapper.getUser(userId);
    }
}
