package org.example.fanzip.notification.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface PushTokenMapper {

    /** 같은 유저-디바이스의 기존 토큰 제거 (uq_user_device 충돌 방지) */
    int deleteByUserAndDevice(@Param("userId") Long userId,
                              @Param("deviceType") String deviceType);

    /** 토큰 기준 업서트 (uq_push_token 충돌 시 소유자/디바이스 이동) */
    int insertOrUpdateByToken(@Param("userId") Long userId,
                              @Param("pushToken") String pushToken,
                              @Param("deviceType") String deviceType);

    /** 특정 인플루언서를 구독 중인 유저들의 디바이스 토큰 조회 (예: deviceType='WEB') */
    List<String> findTokensByInfluencer(@Param("influencerId") Long influencerId,
                                        @Param("status") String status,           // "ACTIVE"
                                        @Param("deviceType") String deviceType);  // "WEB"

    /** 발송 실패 등으로 무효가 된 토큰 정리 */
    int deleteTokens(@Param("tokens") List<String> tokens);

    /** 특정 사용자의 최신 토큰 1개 (디바이스 타입 기준) */
    String findLatestTokenByUserId(@Param("userId") Long userId,
                                   @Param("deviceType") String deviceType);       // "WEB"

    /** 특정 사용자의 FCM 토큰 조회 (QR 코드 생성용) */
    String findTokenByUserId(@Param("userId") Long userId);

    /** 토큰 기준 삭제 (다른 유저가 같은 토큰 보유 시 대비) */
    int deleteByToken(@Param("pushToken") String pushToken);
}
