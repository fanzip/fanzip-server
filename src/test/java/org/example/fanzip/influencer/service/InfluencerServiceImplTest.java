package org.example.fanzip.influencer.service;

import lombok.extern.slf4j.Slf4j;
import org.example.fanzip.config.RootConfig;
import org.example.fanzip.influencer.domain.enums.InfluencerCategory;
import org.example.fanzip.influencer.dto.InfluencerRequestDTO;
import org.example.fanzip.influencer.dto.InfluencerResponseDTO;
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
class InfluencerServiceImplTest {

    @Autowired
    private InfluencerService influencerService;

    @Test
    void findAll_withoutCategory_excludesSubscribedInfluencers() {
        // 5번 유저가 구독중인 인플루언서 제외하고 전체 조회하는 테스트
        
        // given
        InfluencerRequestDTO requestDTO = InfluencerRequestDTO.builder()
                .userId(5L)
                .category(null) // 전체 조회
                .build();

        // when
        List<InfluencerResponseDTO> result = influencerService.findAll(requestDTO);

        // then
        // 5번 유저는 influencer_id 2, 3번을 구독 중
        List<Long> excluded = List.of(2L, 3L);

        // 전체에서 2개 제외(2명 구독중이기 때문에)
        assertEquals(5, result.size());

        for (InfluencerResponseDTO dto : result) {
            System.out.println("조회된 인플루언서 ID: " + dto.getInfluencerId());
            assertFalse(excluded.contains(dto.getInfluencerId()));
        }
    }

    @Test
    void findAll_withCategory_excludesSubscribedInfluencers() {
        // 5번 유저가 구독중인 인플루언서 제외하고 기타 카테고리 인플루언서 조회하는 테스트
        
        // given
        InfluencerRequestDTO requestDTO = InfluencerRequestDTO.builder()
                .userId(5L)
                .category(InfluencerCategory.ETC)
                .build();

        // when
        List<InfluencerResponseDTO> result = influencerService.findAll(requestDTO);

        // then
        // ETC 카테고리 중 2번은 구독 중이므로 제외
        List<Long> expectedIds = List.of(1L, 100L); // 남아있는 ETC 인플루언서

        assertEquals(expectedIds.size(), result.size());

        for (InfluencerResponseDTO dto : result) {
            System.out.println("조회된 인플루언서 ID: " + dto.getInfluencerId());
            assertTrue(expectedIds.contains(dto.getInfluencerId()));
        }
    }


}