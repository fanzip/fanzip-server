package org.example.fanzip.market.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MarketOrderDetailDto {
    private Long orderId;
    private BigDecimal finalAmount;
    private String status;
    private String recipientName;
    private String recipientPhone;
    private String shippingAddress1;
    private String shippingAddress2;
    private String zipcode;
    private LocalDateTime orderedAt;
}