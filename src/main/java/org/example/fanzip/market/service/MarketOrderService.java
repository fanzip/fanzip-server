package org.example.fanzip.market.service;

import org.example.fanzip.market.dto.MarketOrderRequestDto;
import org.example.fanzip.market.dto.MarketOrderResponseDto;

public interface MarketOrderService {
    // 결제 생성
    MarketOrderResponseDto createOrder(Long userId, MarketOrderRequestDto request);

    // 결제 성공된 경우
    void finalizeAfterPaymentApproved(Long orderId);

    // 결제 실패한 경우
    void cleanupAfterPaymentFailed(Long orderId);
}
