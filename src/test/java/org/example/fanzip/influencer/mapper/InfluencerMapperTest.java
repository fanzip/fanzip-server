package org.example.fanzip.influencer.mapper;

import lombok.extern.slf4j.Slf4j;
import org.example.fanzip.config.RootConfig;
import org.example.fanzip.influencer.domain.InfluencerVO;
import org.example.fanzip.influencer.domain.enums.InfluencerCategory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = RootConfig.class)
@Transactional
@Slf4j
class InfluencerMapperTest {

    @Autowired
    private InfluencerMapper influencerMapper;

    @Test
    void findAllFiltered_withoutCategory_excludesSubscribedInfluencers() {

        // 유저 ID 5는 influencer_id 2, 3을 구독 중(테스트 데이터)
        long userId = 5L;

        List<InfluencerVO> result = influencerMapper.findAllFiltered(userId, null);
        log.info("!!!!! 조회 결과 (카테고리 지정 X) !!!!!: {}", result);

        // 총 인플루언서 7명 중, 2명 구독 → 5명 나와야 함
        assertEquals(5, result.size());

        // 구독한 influencer_id 2, 3이 포함되어 있지 않아야 함
        assertFalse(result.stream().anyMatch(i -> i.getInfluencerId() == 2L));
        assertFalse(result.stream().anyMatch(i -> i.getInfluencerId() == 3L));
    }

    @Test
    void findAllFiltered_withCategory_excludesSubscribedInfluencers() {
        // 유저 ID 5는 influencer_id 2, 3 구독 중(테스트 데이터)

        long userId = 5L;
        InfluencerCategory category = InfluencerCategory.ETC;

        List<InfluencerVO> result = influencerMapper.findAllFiltered(userId, category);
        log.info("!!!!! 조회 결과 (카테고리=ETC) !!!!!: {}", result);

        // ETC 카테고리 인플루언서: 1, 2, 100 → 그중 2는 구독중이므로 1, 100만 나와야 함
        assertEquals(2, result.size());
        List<Long> expectedIds = List.of(1L, 100L);

        for (InfluencerVO vo : result) {
            assertTrue(expectedIds.contains(vo.getInfluencerId()));
            assertNotEquals(2L, vo.getInfluencerId()); // 구독 중이라 제외돼야 함
        }
    }

    @Test
    void findAllFiltered_withCategory_noMatchAfterExclusion() {
        // 유저 ID 6은 influencer_id 1, 4 구독 중(테스트 데이터)
        long userId = 6L;
        InfluencerCategory category = InfluencerCategory.TRAVEL; // TRAVEL은 influencer_id 4번

        List<InfluencerVO> result = influencerMapper.findAllFiltered(userId, category);
        log.info("!!!!! 조회 결과 (카테고리=TRAVEL, userId=6) !!!!!: {}", result);

        // 유저 6이 TRAVEL 카테고리인 4번 인플루언서를 구독 중 → 결과 없음
        assertTrue(result.isEmpty());
    }

}