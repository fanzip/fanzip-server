package org.example.fanzip.market.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MarketOrderItemDto {
    private Long productId;
    private Long influencerId;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal shippingPrice;
    private BigDecimal finalPrice;
    private Long cartItemId;
}
