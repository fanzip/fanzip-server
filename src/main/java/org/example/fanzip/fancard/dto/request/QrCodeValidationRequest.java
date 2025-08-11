package org.example.fanzip.fancard.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class QrCodeValidationRequest {
    private String qrData;
    private Double latitude;
    private Double longitude;
}