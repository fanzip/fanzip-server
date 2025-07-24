package org.example.fanzip.membership.mapper;

import lombok.extern.slf4j.Slf4j;
import org.example.fanzip.config.RootConfig;
import org.example.fanzip.membership.domain.MembershipVO;
import org.example.fanzip.membership.domain.enums.MembershipStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;


import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = RootConfig.class)
@Transactional
@Slf4j
class MembershipMapperTest {

    @Autowired
    private MembershipMapper membershipMapper;

    @Test
    void insertMembership() {
        // given

        MembershipVO membership = MembershipVO.builder()
                .userId(999L)
                .influencerId(100L)
                .gradeId(2)
                .status(MembershipStatus.ACTIVE)
                .monthlyAmount(BigDecimal.valueOf(3000.0))
                .totalPaidAmount(BigDecimal.valueOf(3000.0))
                .autoRenewal(true)
                .build();

        // when
        membershipMapper.insertMembership(membership);
        MembershipVO found = membershipMapper.findByUserIdAndInfluencerId(999L, 100L);

        // then
        assertNotNull(found);
        assertEquals(999L, found.getUserId());
        assertEquals(100L, found.getInfluencerId());
        assertEquals(MembershipStatus.ACTIVE, found.getStatus());
        assertNotNull(found.getSubscriptionStart());
        assertNotNull(found.getSubscriptionEnd());
        log.info("구독 성공: {}", found);
    }

    @Test
    void findByUserIdAndInfluencerId() {
        MembershipVO found = membershipMapper.findByUserIdAndInfluencerId(2L, 200L);
        if (found != null) {
            log.info("조회된 구독 정보: {}", found);
            assertEquals(2L, found.getUserId());
        } else {
            log.warn("구독 정보 없음");
        }
    }
}
