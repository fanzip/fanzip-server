package org.example.fanzip.cart.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartItemResponseDto {
    private Long cartItemId;
    private Long productId;
    private Long productOptionId;
    private int quantity;
}
