package org.example.fanzip.cart.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCartItemRequestDto {
    private int quantity;
    private Boolean isSelected;
}
