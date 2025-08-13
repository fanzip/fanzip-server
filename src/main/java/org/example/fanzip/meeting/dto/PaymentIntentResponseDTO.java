package org.example.fanzip.meeting.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentIntentResponseDTO {
    private Long paymentId;
    private BigDecimal amount;
    private long ttlSeconds;
    private Long reservationId;
}
