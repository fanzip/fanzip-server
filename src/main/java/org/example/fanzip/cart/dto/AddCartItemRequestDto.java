package org.example.fanzip.cart.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AddCartItemRequestDto {
    private Long productId;
    private int quantity;
}
