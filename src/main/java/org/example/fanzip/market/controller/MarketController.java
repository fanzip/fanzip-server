package org.example.fanzip.market.controller;

import org.example.fanzip.market.domain.MarketVO;
import org.example.fanzip.market.service.MarketService;
import org.springframework.beans.factory.annotation.Autowired;
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
            @RequestParam(value = "lastProductId", required = false) Long lastProductId
    ) {
        if (lastProductId == null) {
            // 최초 로딩
            return marketService.getAllProducts(limit);
        }
        // 이후 커서 페이징
        return marketService.getProductsAfter(lastProductId, limit);
    }
}
