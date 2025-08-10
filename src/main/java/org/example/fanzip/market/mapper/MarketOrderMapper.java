package org.example.fanzip.market.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.fanzip.market.dto.MarketOrderItemDto;

import java.util.List;
import java.util.Map;

@Mapper
public interface MarketOrderMapper {
    void insertOrder(Map<String, Object> order);
    void insertOrderItems(@Param("orderId") Long orderId,
                          @Param("items") List<MarketOrderItemDto> items);
}
