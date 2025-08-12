package org.example.fanzip.market.controller;

import org.example.fanzip.market.dto.MarketOrderPaymentDto;
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

    // 결제 요청
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MarketOrderResponseDto createOrder(
            @AuthenticationPrincipal CustomUserPrincipal principal,
            @RequestBody MarketOrderRequestDto request
    ) {
        Long userId = principal.getUserId();
        return marketOrderService.createOrder(userId, request);
    }

    // 결제 승인된 경우
    @PostMapping("/{orderId}/on-payment-approved")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void onPaymentApproved(@PathVariable Long orderId) {
        marketOrderService.finalizeAfterPaymentApproved(orderId);
    }

    // 결제 실패
    @PostMapping("/{orderId}/on-payment-failed")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void onPaymentFailed(@PathVariable Long orderId) {
        marketOrderService.cleanupAfterPaymentFailed(orderId);
    }

    @GetMapping("/{orderId}/payment")
    public MarketOrderPaymentDto getOrderPayment(
            @AuthenticationPrincipal CustomUserPrincipal principal,
            @PathVariable Long orderId
    ) {
        Long loginUserId = principal.getUserId();
        return marketOrderService.getOrderPayment(loginUserId, orderId);
    }
}
