package org.example.fanzip.market.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.fanzip.market.domain.enums.ProductCategory;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductListDto {
    private Long productId;
    private String name;
    private BigDecimal price;
    private BigDecimal discountedPrice;
    private BigDecimal discountRate;
    private String thumbnailImage;
    private Integer stock;
    private String categories;
}
