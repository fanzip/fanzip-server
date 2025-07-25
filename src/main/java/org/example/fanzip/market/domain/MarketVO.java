package org.example.fanzip.market.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MarketVO {
    private Long productId;
    private Long influencerId;
    private String name;
    private String description;
    private BigDecimal price;
    private BigDecimal discountedPrice;
    private BigDecimal discountRate;
    private String thumbnailImage;

    private Integer stock;
    private Boolean isSoldOut;

    private BigDecimal shippingPrice;
    private String detailImages;
    private String descriptionImages;

    private LocalDateTime whiteOpenTime;
    private LocalDateTime silverOpenTime;
    private LocalDateTime goldOpenTime;
    private LocalDateTime vipOpenTime;
    private LocalDateTime generalOpenTime;
}
