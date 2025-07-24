package org.example.fanzip.user.service;

import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.example.fanzip.user.dto.AdditionalInfoDTO;
import org.example.fanzip.user.dto.UserDTO;
import org.example.fanzip.user.mapper.UserMapper;
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
    public Long register(AdditionalInfoDTO dto){
        UserDTO newUser=UserDTO.builder()
                .socialType(dto.getSocialType())
                .socialId(dto.getSocialId())
                .name(dto.getName())
                .phone(dto.getPhone())
                .email(dto.getEmail())
                .created_at(new Date())
                .build();
        mapper.insertUser(newUser);
        System.out.println("userId: "+newUser.getUserId());
        return newUser.getUserId();
    }
//    void updateAdditionalInfo(String socialType, String socialId, AdditionalInfoDTO additionalInfoDTO){
//        mapper.updateAdditionalInfo(socialType, socialId, additionalInfoDTO);
//    }
}
