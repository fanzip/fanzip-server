package org.example.fanzip.market.controller;

import org.example.fanzip.market.dto.MarketOrderRequestDto;
import org.example.fanzip.market.dto.MarketOrderResponseDto;
import org.example.fanzip.market.service.MarketOrderService;
import org.example.fanzip.security.CustomUserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/market/orders")
public class MarketOrderController {
    private final MarketOrderService marketOrderService;

    @Autowired
    public MarketOrderController(MarketOrderService marketOrderService) {
        this.marketOrderService = marketOrderService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MarketOrderResponseDto createOrder(
            @AuthenticationPrincipal CustomUserPrincipal principal,
            @RequestBody MarketOrderRequestDto request
    ) {
        Long userId = principal.getUserId();
        return marketOrderService.createOrder(userId, request);
    }
}
