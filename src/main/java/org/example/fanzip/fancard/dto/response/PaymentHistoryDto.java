package org.example.fanzip.fancard.dto.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PaymentHistoryDto {
    private Long paymentId;
    private String title;
    private BigDecimal amount;
    private LocalDateTime paidAt;
    private String status;
    private String paymentMethod;
    private String description;
    private Boolean bold;
}