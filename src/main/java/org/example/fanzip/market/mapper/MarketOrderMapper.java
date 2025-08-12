package org.example.fanzip.market.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.fanzip.market.dto.MarketOrderItemDto;
import org.example.fanzip.market.dto.MarketOrderPaymentDto;
import org.springframework.security.core.parameters.P;

import java.util.List;
import java.util.Map;

@Mapper
public interface MarketOrderMapper {
    // 결제 요청
    void insertOrder(Map<String, Object> order);
    void insertOrderItems(@Param("orderId") Long orderId,
                          @Param("items") List<MarketOrderItemDto> items);

    // 결제 승인
    List<MarketOrderItemDto> selectOrderItems(@Param("orderId") Long orderId);
    List<Long> selectCartItemIdsByOrderId(@Param("orderId") Long orderId);
    int deleteCartItemsByIds(@Param("ids") List<Long> cartItemIds);
    int updateOrderStatus(@Param("orderId") Long orderId,
                          @Param("status") String status);
    // 재고 차감
    int decreaseProductStock(@Param("productId") Long productId, @Param("quantity") Integer quantity);

    // DB lock (currentStatus 일때만 newStatus로 변경)
    int updateOrderStatusIfCurrent(@Param("orderId") Long orderId,
                                   @Param("newStatus") String newStatus,
                                   @Param("currentStatus") String currentStatus);

    // 현재 상태 조회
    String selectOrderStatus(@Param("orderId") Long orderId);

    // 결제 실패
    int deleteOrderItemsByOrderId(@Param("orderId") Long orderId);
    int deleteOrderById(@Param("orderId") Long orderId);

    // payment 연동
    Map<String, Object> selectOrderForPayment(@Param("orderId") Long orderId);
}
