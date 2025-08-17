package org.example.fanzip.payment.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.fanzip.payment.dto.PaymentRequestDto;
import org.example.fanzip.payment.dto.PaymentResponseDto;
import org.example.fanzip.payment.service.PaymentService;
import org.example.fanzip.security.CustomUserPrincipal;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@Slf4j
public class PaymentController {
    private final PaymentService paymentService;

    @PostMapping("/request")
    public ResponseEntity<PaymentResponseDto> createPayment(
            @AuthenticationPrincipal CustomUserPrincipal principal,
            @RequestBody PaymentRequestDto requestDto){
        
        // JWT 토큰에서 실제 user_id 추출하여 덮어쓰기 (보안상 중요!)
        Long authenticatedUserId = principal.getUserId();
        log.info("결제 요청 - 인증된 사용자 ID: {}, 요청 DTO의 user_id: {}", 
                authenticatedUserId, requestDto.getUserId());
        
        // 보안을 위해 인증된 user_id로 강제 덮어쓰기
        PaymentRequestDto secureRequestDto = PaymentRequestDto.builder()
                .userId(authenticatedUserId)  // 인증된 사용자 ID 사용
                .orderId(requestDto.getOrderId())
                .reservationId(requestDto.getReservationId())
                .membershipId(requestDto.getMembershipId())
                .influencerId(requestDto.getInfluencerId())
                .transactionId(requestDto.getTransactionId())
                .paymentType(requestDto.getPaymentType())
                .paymentMethod(requestDto.getPaymentMethod())
                .amount(requestDto.getAmount())
                .build();
        
        PaymentResponseDto responseDto = paymentService.createPayment(secureRequestDto);
        return ResponseEntity.ok(responseDto);
    }
    @PatchMapping("/{paymentId}/approve")
    public ResponseEntity<PaymentResponseDto> approvePayment(@PathVariable Long paymentId){
        PaymentResponseDto responseDto = paymentService.approvePaymentById(paymentId);
        return ResponseEntity.ok(responseDto);
    }
    @PatchMapping("/{paymentId}/fail")
    public ResponseEntity<PaymentResponseDto> failedPayment(@PathVariable Long paymentId){
        PaymentResponseDto responseDto = paymentService.failedPaymentById(paymentId);
        return ResponseEntity.ok(responseDto);
    }
    @PatchMapping("/{paymentId}/cancelled")
    public ResponseEntity<PaymentResponseDto> cancelled(@PathVariable Long paymentId){
        PaymentResponseDto responseDto= paymentService.cancelledPaymentById(paymentId);
        return ResponseEntity.ok(responseDto);
    }
    @PatchMapping("/{paymentId}/refunded")
    public ResponseEntity<PaymentResponseDto> refunded(@PathVariable Long paymentId){
        PaymentResponseDto responseDto = paymentService.refundedPaymentById(paymentId);
        return ResponseEntity.ok(responseDto);
    }
    @GetMapping("/{paymentId}")
    public ResponseEntity<PaymentResponseDto> getPaymentDetail(@PathVariable Long paymentId){
        PaymentResponseDto responseDto = paymentService.getPayment(paymentId);
        return ResponseEntity.ok(responseDto);
    }
    @GetMapping("/user/{userId}") // 로그인 인증 구현 후 변경 예정
    public ResponseEntity<List<PaymentResponseDto>> getMyPaymentDetail(@PathVariable Long userId){
        List<PaymentResponseDto> responseDtoList = paymentService.getMyPayments(userId);
        return ResponseEntity.ok(responseDtoList);
    }
}
