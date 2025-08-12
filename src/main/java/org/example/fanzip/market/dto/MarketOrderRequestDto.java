package org.example.fanzip.market.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MarketOrderRequestDto {
    private BigDecimal finalAmount;
    private String recipientName;
    private String recipientPhone;
    private String shippingAddress1;
    private String shippingAddress2;
    private String zipcode;
    private String paymentMethod;
    private String orderType; // "cart"(장바구니) / "buy"(바로구매)
    private List<MarketOrderItemDto> items;
}
