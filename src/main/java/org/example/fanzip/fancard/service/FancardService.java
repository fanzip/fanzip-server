package org.example.fanzip.fancard.service;

import org.example.fanzip.fancard.dto.request.QrCodeRequest;
import org.example.fanzip.fancard.dto.request.QrCodeValidationRequest;
import org.example.fanzip.fancard.dto.response.FancardDetailResponse;
import org.example.fanzip.fancard.dto.response.FancardListWrapper;
import org.example.fanzip.fancard.dto.response.QrCodeResponse;
import org.example.fanzip.fancard.dto.response.QrCodeValidationResponse;
import org.example.fanzip.fancard.dto.response.PaymentHistoryDto;

import java.util.List;

public interface FancardService {
    
    FancardListWrapper getUserFancards(Long userId);
    
    FancardDetailResponse getFancardDetail(Long cardId);
    
    QrCodeResponse generateQrCode(QrCodeRequest request);
    
    QrCodeValidationResponse validateQrCode(QrCodeValidationRequest request);
    
    QrCodeResponse getMobileTicketData(Long userId, Long reservationId, Long seatId, Long meetingId);
    
    void createFancardForMembership(Long membershipId, Long influencerId);
    
    List<PaymentHistoryDto> getPaymentHistory(Long membershipId);
}
