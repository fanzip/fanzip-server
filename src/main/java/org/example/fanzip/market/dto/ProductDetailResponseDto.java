package org.example.fanzip.market.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductDetailResponseDto {
    private Long productId;
    private String name;
    private BigDecimal price;
    private BigDecimal discountedPrice;
    private BigDecimal shippingPrice;
    private String description;
    private String thumbnailImage;
    private List<String> detailImages;
    private List<ProductOptionDto> options; // 현재는 빈 배열로 설정됨
}
