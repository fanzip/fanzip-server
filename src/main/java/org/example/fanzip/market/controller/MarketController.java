package org.example.fanzip.market.controller;

import org.example.fanzip.auth.jwt.JwtProcessor;
import org.example.fanzip.market.dto.ProductDetailDto;
import org.example.fanzip.market.dto.ProductListDto;
import org.example.fanzip.market.service.MarketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/market")
public class MarketController {

    private final MarketService marketService;
    private final JwtProcessor jwtProcessor;

    @Autowired
    public MarketController(MarketService marketService, JwtProcessor jwtProcessor) {
        this.marketService = marketService;
        this.jwtProcessor = jwtProcessor;
    }

    // 전체 상품 조회
    @GetMapping("/products")
    public List<ProductListDto> getProducts(
            @RequestParam(value = "limit", defaultValue = "20") int limit,
            @RequestParam(value = "lastProductId", required = false) Long lastProductId,
            @RequestParam(value = "q", required = false) String keyword
    ) {
        if(keyword != null && !keyword.isBlank()) {
            return marketService.searchProducts(keyword, lastProductId, limit);
        }

        if (lastProductId == null) {
            return marketService.getAllProducts(limit);
        }
        return marketService.getProductsAfter(lastProductId, limit);
    }

    // 상세 상품 조회
    @GetMapping("/products/{productId}")
    public ResponseEntity<ProductDetailDto> getProductDetail(
            //@RequestHeader("Authorization") String header,
            @RequestHeader("X-USER-ID") Long userId,
            @PathVariable Long productId
    ) {
//        String token = header.substring(7);
//        Long userId = jwtProcessor.getUserId(token);
        ProductDetailDto dto = marketService.getProductDetail(userId, productId);
        return ResponseEntity.ok(dto);
    }
}
