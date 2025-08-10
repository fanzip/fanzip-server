package org.example.fanzip.market.service;

import org.example.fanzip.market.dto.MarketOrderRequestDto;
import org.example.fanzip.market.dto.MarketOrderResponseDto;

public interface MarketOrderService {
    MarketOrderResponseDto createOrder(Long userId, MarketOrderRequestDto request);
}
