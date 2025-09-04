package org.example.fanzip.market.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MarketOrderItemResponseDto {
    private Long productId;
    private Long influencerId;
    private String productName;
    private String thumbnailImage;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal shippingPrice;
    private BigDecimal finalPrice;
    private BigDecimal originalPrice;
}