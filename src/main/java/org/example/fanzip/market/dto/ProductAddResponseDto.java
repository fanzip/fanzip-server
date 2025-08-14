package org.example.fanzip.market.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductAddResponseDto {
    
    private Long productId;
    private Long influencerId;
    private String name;
    private String description;
    private BigDecimal price;
    private BigDecimal discountedPrice;
    private BigDecimal discountRate;
    private BigDecimal shippingPrice;
    private Integer stock;
    private String thumbnailImage;
    private LocalDateTime createdAt;
    private String categories;

    // API 응답 메시지
    private String message;
    private boolean success;
    
    public static ProductAddResponseDto success(Long productId, String name, String message) {
        return ProductAddResponseDto.builder()
                .productId(productId)
                .name(name)
                .message(message)
                .success(true)
                .build();
    }
    
    public static ProductAddResponseDto failure(String message) {
        return ProductAddResponseDto.builder()
                .message(message)
                .success(false)
                .build();
    }
}