package org.example.fanzip.cart.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartItemDto {
    private Long cartItemId;
    private Long productId;
    private String productName;
    private String thumbnailImage;
    private BigDecimal unitPrice; // 정가
    private BigDecimal discountedPrice;
    private int quantity; // 선택된 수량
    private Boolean isSelected;
    private BigDecimal totalPrice;
    private Long influencerId;
    private String influencerName;
    private BigDecimal shippingPrice;
}
