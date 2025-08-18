package org.example.fanzip.market.controller;

import org.example.fanzip.market.dto.MarketOrderDetailDto;
import org.example.fanzip.market.dto.MarketOrderItemResponseDto;
import org.example.fanzip.market.dto.MarketOrderPaymentDto;
import org.example.fanzip.market.dto.MarketOrderRequestDto;
import org.example.fanzip.market.dto.MarketOrderResponseDto;
import org.example.fanzip.market.service.MarketOrderService;
import org.example.fanzip.security.CustomUserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

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

    // 주문 완료 페이지용 주문 상세 정보 조회
    @GetMapping("/{orderId}")
    public MarketOrderDetailDto getOrderDetail(
            @AuthenticationPrincipal CustomUserPrincipal principal,
            @PathVariable String orderId
    ) {
        Long loginUserId = principal.getUserId();
        
        // orderId 파싱: "order_4_12" -> 4 추출
        Long actualOrderId = parseOrderId(orderId);
        
        return marketOrderService.getOrderDetail(loginUserId, actualOrderId);
    }

    // 주문 완료 페이지용 주문 상품 조회
    @GetMapping("/{orderId}/items")
    public List<MarketOrderItemResponseDto> getOrderItems(
            @AuthenticationPrincipal CustomUserPrincipal principal,
            @PathVariable String orderId
    ) {
        Long loginUserId = principal.getUserId();
        
        // orderId 파싱: "order_4_12" -> 4 추출
        Long actualOrderId = parseOrderId(orderId);
        
        return marketOrderService.getOrderItems(loginUserId, actualOrderId);
    }
    
    private Long parseOrderId(String orderId) {
        // "order_4_12" 형태에서 실제 order_id인 4를 추출
        if (orderId.startsWith("order_")) {
            String[] parts = orderId.substring(6).split("_"); // "order_" 제거 후 "_"로 분할
            if (parts.length > 0) {
                try {
                    return Long.parseLong(parts[0]); // 첫 번째 숫자가 실제 order_id
                } catch (NumberFormatException e) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid orderId format: " + orderId);
                }
            }
        }
        
        // 숫자만 있는 경우 그대로 파싱
        try {
            return Long.parseLong(orderId);
        } catch (NumberFormatException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid orderId format: " + orderId);
        }
    }
}
