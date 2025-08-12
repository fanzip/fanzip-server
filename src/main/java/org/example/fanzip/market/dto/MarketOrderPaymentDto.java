package org.example.fanzip.market.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MarketOrderPaymentDto {
    private Long orderId;
    private Long userId;
    private BigDecimal finalAmount;
}
