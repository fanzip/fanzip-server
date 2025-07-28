package org.example.fanzip.cart.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddCartItemRequestDto {
    private Long productId;
    private int quantity;
}
