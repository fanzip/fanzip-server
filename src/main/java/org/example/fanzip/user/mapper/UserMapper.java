package org.example.fanzip.user.mapper;

import org.apache.ibatis.annotations.Param;
import org.example.fanzip.user.dto.UserDTO;

public interface UserMapper {
    UserDTO findBySocialTypeAndSocialId(@Param("socialType") String socialType,
                                        @Param("socialId") String socialId);
    void insertUser(UserDTO user);
//    void updateAdditionalInfo(@Param("socialType") String socialType,
//                              @Param("socialId") String socialId,
//                              @Param("additionalInfoDTO") AdditionalInfoDTO additionalInfoDTO);
}
