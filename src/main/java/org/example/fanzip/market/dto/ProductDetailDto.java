package org.example.fanzip.market.dto;

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
public class ProductDetailDto {
    private Long productId;
    private Long influencerId;
    private String name;
    private Integer stock;
    private BigDecimal price;
    private BigDecimal discountedPrice;
    private BigDecimal discountRate;
    private BigDecimal shippingPrice;

    private String thumbnailImage;
    private String detailImages;
    private String descriptionImages;
    private List<String> detailImagesList;
    private List<String> descriptionImagesList;

    private Integer gradeId;
    private LocalDateTime openTime;
    private boolean isAvailable;

}
