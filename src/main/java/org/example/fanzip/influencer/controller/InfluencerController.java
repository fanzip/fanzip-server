package org.example.fanzip.influencer.controller;


import lombok.RequiredArgsConstructor;
import org.example.fanzip.influencer.domain.enums.InfluencerCategory;
import org.example.fanzip.influencer.dto.InfluencerRequestDTO;
import org.example.fanzip.influencer.dto.InfluencerResponseDTO;
import org.example.fanzip.influencer.service.InfluencerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/influencers")
public class InfluencerController {

    private final InfluencerService influencerService;

    // 전체 인플루언서 목록 조회 (필터: 선택적 카테고리 지정 가능)
    @GetMapping
    public ResponseEntity<List<InfluencerResponseDTO>> getInfluencers(
            // 카테고리 지정하면 /api/influencers/?category= 형태로 조회
            @RequestParam(value = "category", required = false) InfluencerCategory category,
            HttpServletRequest request) {

        // JWT Interceptor에서 설정한 userId를 가져옴 (구독한 인플루언서 제외용)
        Long userId = (Long) request.getAttribute("userId");
        
        InfluencerRequestDTO requestDTO = InfluencerRequestDTO.builder()
                .userId(userId)
                .category(category)
                .build();
        
        List<InfluencerResponseDTO> influencerList = influencerService.findAll(requestDTO);

        return ResponseEntity.ok(influencerList);

    }
}
