package org.example.fanzip.market.controller;

import lombok.extern.slf4j.Slf4j;
import org.example.fanzip.market.dto.ProductAddRequestDto;
import org.example.fanzip.market.dto.ProductAddResponseDto;
import org.example.fanzip.market.dto.ProductDetailDto;
import org.example.fanzip.market.dto.ProductListDto;
import org.example.fanzip.market.service.MarketService;
import org.example.fanzip.security.CustomUserPrincipal;
import org.example.fanzip.security.JwtProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
@Slf4j
@RestController
@RequestMapping("/api/market")
public class MarketController {

    private final MarketService marketService;

    @Autowired
    public MarketController(MarketService marketService, JwtProcessor jwtProcessor) {
        this.marketService = marketService;
    }

    // 전체 상품 조회
    @GetMapping("/products")
    public List<ProductListDto> getProducts(
            @AuthenticationPrincipal CustomUserPrincipal customUserPrincipal,
            @RequestParam(value = "limit", defaultValue = "20") int limit,
            @RequestParam(value = "lastProductId", required = false) Long lastProductId,
            @RequestParam(value = "q", required = false) String keyword,
            @RequestParam(value="sort", defaultValue = "recommended") String sort,
            @RequestParam(value = "category", required = false) String category,
            @RequestParam(value = "onlySubscribed", required = false, defaultValue = "false") boolean onlySubscribed
    ) {
        Long userId = customUserPrincipal.getUserId();

        if(keyword != null && !keyword.isBlank()) {
            return marketService.searchProducts(userId, keyword, lastProductId, limit, sort, category);
        }

        return marketService.getProducts(userId, lastProductId, limit, sort, category, onlySubscribed);
    }

    // 상세 상품 조회
    @GetMapping("/products/{productId}")
    public ResponseEntity<ProductDetailDto> getProductDetail(
            @AuthenticationPrincipal CustomUserPrincipal principal,
            @PathVariable Long productId
    ) {
        Long userId = principal.getUserId();
        ProductDetailDto dto = marketService.getProductDetail(userId, productId);
        return ResponseEntity.ok(dto);
    }

    // 상품 추가
    @PostMapping("/products")
    public ResponseEntity<ProductAddResponseDto> addProduct(
            @AuthenticationPrincipal CustomUserPrincipal principal,
            @RequestBody ProductAddRequestDto requestDto
    ) {
        try {
            // 현재 사용자 정보 확인
            Long userId = principal.getUserId();
            log.info("상품 추가 요청 - 사용자 ID: {}, 요청 데이터: {}", userId, requestDto);
            
            // 상품 추가 서비스 호출
            ProductAddResponseDto response = marketService.addProduct(requestDto);
            
            if (response.isSuccess()) {
                log.info("상품 추가 성공 - 상품 ID: {}", response.getProductId());
                return ResponseEntity.status(HttpStatus.CREATED).body(response);
            } else {
                log.warn("상품 추가 실패: {}", response.getMessage());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
            
        } catch (Exception e) {
            log.error("상품 추가 중 예외 발생", e);
            ProductAddResponseDto errorResponse = ProductAddResponseDto.failure(
                    "상품 추가 중 서버 오류가 발생했습니다."
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}
