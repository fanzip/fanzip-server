package org.example.fanzip.market.service;

import org.example.fanzip.market.dto.MarketOrderDetailDto;
import org.example.fanzip.market.dto.MarketOrderItemResponseDto;
import org.example.fanzip.market.dto.MarketOrderPaymentDto;
import org.example.fanzip.market.dto.MarketOrderRequestDto;
import org.example.fanzip.market.dto.MarketOrderResponseDto;

import java.util.List;

public interface MarketOrderService {
    // 결제 생성
    MarketOrderResponseDto createOrder(Long userId, MarketOrderRequestDto request);

    // 결제 성공된 경우
    void finalizeAfterPaymentApproved(Long orderId);

    // 결제 실패한 경우
    void cleanupAfterPaymentFailed(Long orderId);

    MarketOrderPaymentDto getOrderPayment(Long requestUserId, Long orderId);

    // 주문 완료 페이지용 주문 상품 조회
    List<MarketOrderItemResponseDto> getOrderItems(Long requestUserId, Long orderId);

    // 주문 완료 페이지용 주문 상세 정보 조회
    MarketOrderDetailDto getOrderDetail(Long requestUserId, Long orderId);
}
