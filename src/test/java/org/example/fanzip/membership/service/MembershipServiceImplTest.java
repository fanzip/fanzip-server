package org.example.fanzip.membership.service;

import lombok.extern.slf4j.Slf4j;
import org.example.fanzip.config.RootConfig;
import org.example.fanzip.membership.dto.MembershipSubscribeRequestDTO;
import org.example.fanzip.membership.dto.MembershipSubscribeResponseDTO;
import org.example.fanzip.membership.mapper.MembershipMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = RootConfig.class)
class MembershipServiceImplTest {

    @Autowired
    private MembershipMapper membershipMapper;

    @Autowired
    private MembershipService membershipService;

    @Test
    void subscribe() {
        // given
        long userId = 1234L;
        long influencerId = 5678L;

        // 중복 제거: 기존 구독 삭제 (중복 구독 방지)
        membershipMapper.deleteByUserIdAndInfluencerId(userId, influencerId);

        MembershipSubscribeRequestDTO requestDTO = MembershipSubscribeRequestDTO.builder()
                .influencerId(1L)
                .gradeId(2)  // 실버
                .monthlyAmount(BigDecimal.valueOf(9900))
                .build();

        // when
        MembershipSubscribeResponseDTO responseDTO = membershipService.subscribe(requestDTO, userId);

        // then
        assertNotNull(responseDTO);
        assertEquals("ACTIVE", responseDTO.getStatus().name());
        log.info("구독 성공: {}", responseDTO);
    }
}
