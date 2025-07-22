package org.example.fanzip.market.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

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

    // 상세 페이지용 필드
    private BigDecimal shippingPrice;
    private String description;
    private String detailImages; // JSON 배열 형태로 들어올 경우 String으로 처리
}
