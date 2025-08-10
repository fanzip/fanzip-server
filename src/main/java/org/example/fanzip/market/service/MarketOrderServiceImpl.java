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
import java.util.List;
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
        Number key = (Number) orderData.get("orderId");
        if(key == null) throw new IllegalStateException("orderId is null");
        Long orderId = key.longValue();

        // 주문 상품(order_itmes) 저장
        marketOrderMapper.insertOrderItems(orderId, request.getItems());

        return new MarketOrderResponseDto(orderId);
    }

    // 결제 성공 -> 재고차감, 카트에서 삭제, status 업데이트
    @Override
    @Transactional
    public void finalizeAfterPaymentApproved(Long orderId) {
        // lock
        int locked = marketOrderMapper.updateOrderStatusIfCurrent(orderId, "PROCESSING", "PENDING");
        if(locked == 0) {
            String cur = marketOrderMapper.selectOrderStatus(orderId);
            if("PAID".equals(cur)){
                return;
            }
            // 이미 다른 트랜잭션에서 처리중
            if("PROCESSING".equals(cur)){
                return;
            }
            // 실패/취소된 주문 -> 처리X
            if("FAILED".equals(cur) || "CANCELLED".equals(cur)){
                return;
            }
            // 모르는 status
            throw new IllegalStateException("Unexpected order status: " + cur);
        }

        // 주문 상품 로드
        List<MarketOrderItemDto> items = marketOrderMapper.selectOrderItems(orderId);
        if(items == null || items.isEmpty()) {throw new IllegalArgumentException("order item is null");}

        // 재고 차감
        for(MarketOrderItemDto item: items) {
            int ok = marketOrderMapper.decreaseProductStock(item.getProductId(), item.getQuantity());
            if(ok == 0) {
                throw new IllegalStateException("재고 부족/변경됨: productId: " + item.getProductId());
            }
        }

        // 장바구니에서 삭제
        List<Long> cartItemIds = marketOrderMapper.selectCartItemIdsByOrderId(orderId);
        if(cartItemIds != null && !cartItemIds.isEmpty()) {
            marketOrderMapper.deleteCartItemsByIds(cartItemIds);
        }

        // status 업데이트 (PROCESSING->PAID)
        int done = marketOrderMapper.updateOrderStatusIfCurrent(orderId, "PAID", "PROCESSING");
        if(done == 0) {
            String cur = marketOrderMapper.selectOrderStatus(orderId);
            if(!"PAID".equalsIgnoreCase(cur)){
                throw new IllegalStateException("status trans failed. cur=" + cur);
            }
        }
    }

    @Override
    @Transactional
    public void cleanupAfterPaymentFailed(Long orderId) {
        String cur = marketOrderMapper.selectOrderStatus(orderId);
        // 이미 결제 완료된 경우
        if("PAID".equals(cur)){
            return;
        }
        marketOrderMapper.deleteOrderItemsByOrderId(orderId);
        marketOrderMapper.deleteOrderById(orderId);
    }
}
