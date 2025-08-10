package org.example.fanzip.market.service;

import org.example.fanzip.cart.mapper.CartMapper;
import org.example.fanzip.market.dto.MarketOrderItemDto;
import org.example.fanzip.market.dto.MarketOrderRequestDto;
import org.example.fanzip.market.dto.MarketOrderResponseDto;
import org.example.fanzip.market.mapper.MarketMapper;
import org.example.fanzip.market.mapper.MarketOrderMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;

@Service
@Transactional
public class MarketOrderServiceImpl implements MarketOrderService {
    private final MarketOrderMapper marketOrderMapper;
    private final MarketMapper marketMapper;
    private final CartMapper cartMapper;

    @Autowired
    public MarketOrderServiceImpl(MarketOrderMapper marketOrderMapper,
                                  MarketMapper marketMapper,
                                  CartMapper cartMapper) {
        this.marketOrderMapper = marketOrderMapper;
        this.marketMapper = marketMapper;
        this.cartMapper = cartMapper;
    }

    @Override
    public MarketOrderResponseDto createOrder(Long userId, MarketOrderRequestDto request) {
        // 재고 확인
        for (MarketOrderItemDto item : request.getItems()) {
            int stock = marketMapper.getStock(item.getProductId());
            if(stock < item.getQuantity()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "상품 재고가 부족합니다. 상품ID: " + item.getProductId());
            }
        }

        // 주문(orders) 생성
        Map<String, Object> orderData = new HashMap<>();
        orderData.put("userId", userId);
        orderData.put("finalAmount", request.getFinalAmount());
        orderData.put("status", "PENDING");
        orderData.put("recipientName", request.getRecipientName());
        orderData.put("recipientPhone", request.getRecipientPhone());
        orderData.put("shippingAddress1", request.getShippingAddress1());
        orderData.put("shippingAddress2", request.getShippingAddress2());
        orderData.put("zipcode", request.getZipcode());

        marketOrderMapper.insertOrder(orderData);
//        Long orderId = (Long) orderData.get("orderId");
        Number key = (Number) orderData.get("orderId");
        if(key == null) throw new IllegalStateException("orderId is null");
        Long orderId = key.longValue();

        // 주문 상품(order_itmes) 저장
        marketOrderMapper.insertOrderItems(orderId, request.getItems());

        // 장바구니에서 삭제
        if("cart".equals(request.getOrderType())) {
            for(MarketOrderItemDto item : request.getItems()) {
                if(item.getCartItemId() != null) {
                    Integer count = cartMapper.checkCartItem(userId, item.getCartItemId());
                    if(count > 0) {
                        cartMapper.deleteCartItem(item.getCartItemId());
                    }
                }
            }
        }
        return new MarketOrderResponseDto(orderId);
    }
}
