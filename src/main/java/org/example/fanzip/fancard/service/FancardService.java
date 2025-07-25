package org.example.fanzip.fancard.service;

import org.example.fanzip.fancard.dto.response.FancardDetailResponse;
import org.example.fanzip.fancard.dto.response.FancardListWrapper;
import org.example.fanzip.fancard.dto.response.QrCodeResponse;

public interface FancardService {
    
    FancardListWrapper getUserFancards(Long userId);
    
    FancardDetailResponse getFancardDetail(Long cardId);
    
    QrCodeResponse generateQrCode(Long reservationId);
}
