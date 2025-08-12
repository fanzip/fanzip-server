package org.example.fanzip.cart.dto;

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
public class CartResponseDto {
    private List<CartItemDto> items;
    // 선택 항목만 최종 금액
    private BigDecimal grandTotal;

    // 유저 정보 (배송관련)
    private String address1;
    private String address2;
    private String zipcode;
    private String name;
    private String phone;
}
