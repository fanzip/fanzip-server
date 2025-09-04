package org.example.fanzip.fancard.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

@Service
public class QrCodeGeneratorService {
    
    private static final int QR_CODE_WIDTH = 300;
    private static final int QR_CODE_HEIGHT = 300;
    
    /**
     * QR 코드 데이터로부터 Base64 인코딩된 이미지를 생성
     */
    public String generateQrCodeImage(String qrData) {
        try {
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(qrData, BarcodeFormat.QR_CODE, QR_CODE_WIDTH, QR_CODE_HEIGHT);
            
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream);
            
            byte[] qrCodeBytes = outputStream.toByteArray();
            return Base64.getEncoder().encodeToString(qrCodeBytes);
            
        } catch (WriterException | IOException e) {
            throw new RuntimeException("QR 코드 생성 중 오류가 발생했습니다.", e);
        }
    }
    
    /**
     * QR 코드 데이터 문자열 생성 (FCM 토큰 포함)
     */
    public String generateQrDataString(Long userId, Long fanMeetingId, Long reservationId, String timestamp, String fcmToken) {
        // FCM 토큰이 null이거나 빈 문자열인 경우 "NO_TOKEN"으로 대체
        String tokenPart = (fcmToken != null && !fcmToken.trim().isEmpty()) ? fcmToken : "NO_TOKEN";
        return String.format("FANZIP_%d_%d_%d_%s_%s", userId, fanMeetingId, reservationId, timestamp, tokenPart);
    }
    
    /**
     * QR 코드 데이터 문자열 생성 (기존 호환성을 위한 메서드)
     */
    public String generateQrDataString(Long userId, Long fanMeetingId, Long reservationId, String timestamp) {
        return generateQrDataString(userId, fanMeetingId, reservationId, timestamp, "NO_TOKEN");
    }
}