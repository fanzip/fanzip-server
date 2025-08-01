package org.example.fanzip.market.controller;


import org.example.fanzip.market.dto.ProductDetailDto;
import org.example.fanzip.market.dto.ProductListDto;
import org.example.fanzip.market.service.MarketService;
import org.example.fanzip.security.JwtProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

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
            @RequestHeader("X-USER-ID") Long userId,
//            HttpServletRequest request,
            @PathVariable Long productId
    ) {
//        Long userId = (Long) request.getAttribute("userId");
        ProductDetailDto dto = marketService.getProductDetail(userId, productId);
        return ResponseEntity.ok(dto);
    }
}
