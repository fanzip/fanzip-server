package org.example.fanzip.market.controller;

import org.example.fanzip.market.domain.MarketVO;
import org.example.fanzip.market.dto.ProductDetailResponseDto;
import org.example.fanzip.market.service.MarketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/market")
public class MarketController {

    private final MarketService marketService;

    @Autowired
    public MarketController(MarketService marketService) {
        this.marketService = marketService;
    }

    @GetMapping("/products")
    public List<MarketVO> getProducts(
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

    @GetMapping("/products/{productId}")
    public ResponseEntity<ProductDetailResponseDto> getProductDetail(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long productId
    ) {
        ProductDetailResponseDto dto = marketService.getProductDetail(userId, productId);
        return ResponseEntity.ok(dto);
    }
}
