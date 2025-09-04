package org.example.fanzip.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TossApproveRequestDto {
    private String paymentKey;
    private String orderId;
    private int amount;
}
