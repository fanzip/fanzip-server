package org.example.fanzip.market.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

// 상품 목록 조회를 위한 VO
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MarketVO {
    private Long productId;
    private String name;
    private BigDecimal price;
    private BigDecimal discountedPrice;
    private BigDecimal discountRate;
    private String thumbnailImage;
    private Boolean isSoldOut;
}
