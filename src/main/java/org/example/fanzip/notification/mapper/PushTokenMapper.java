package org.example.fanzip.notification.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PushTokenMapper {
    // ① 같은 유저-디바이스의 기존 토큰 제거 (uq_user_device 충돌 방지)
    int deleteByUserAndDevice(@Param("userId") Long userId,
                              @Param("deviceType") String deviceType);

    // ② 토큰 기준으로 insert 또는 소유자/디바이스 업데이트 (uq_push_token 충돌 시 소유자 이동)
    int insertOrUpdateByToken(@Param("userId") Long userId,
                              @Param("pushToken") String pushToken,
                              @Param("deviceType") String deviceType);

    // 특정 인플루언서를 구독 중인 유저들의 디바이스 토큰 조회
    List<String> findTokensByInfluencer(@Param("influencerId") Long influencerId,
                                        @Param("status") String status); // ex) "ACTIVE"

    // 발송실패 등으로 무효가 된 토큰 정리
    int deleteTokens(@Param("tokens") List<String> tokens);
}
