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
public class CreateMarketOrderRequestDto {
    private Long userId;
    private BigDecimal finalAmount;
    private String status;
    private String recipientName;
    private String recipientPhone;
    private String shippingAddress1;
    private String shippingAddress2;
    private String zipcode;
    private String paymentMethod;
    private List<CartItemDto> items;
}
