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
    private Long userId;
    private BigDecimal finalAmount;
    private String status;
    private String recipientName;
    private String recipientPhone;
    private String shippingAddress1;
    private String shippingAddress2;
    private String zipcode;
    private List<MarketOrderItemDto> items;
}
