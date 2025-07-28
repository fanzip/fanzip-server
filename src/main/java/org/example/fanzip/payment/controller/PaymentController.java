package org.example.fanzip.payment.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.fanzip.payment.dto.PaymentRequestDto;
import org.example.fanzip.payment.dto.PaymentResponseDto;
import org.example.fanzip.payment.service.PaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@Slf4j
public class PaymentController {
    private final PaymentService paymentService;

    @PostMapping("/request")
    public ResponseEntity<PaymentResponseDto> createPayment(@RequestBody PaymentRequestDto requestDto){
        PaymentResponseDto responseDto = paymentService.createPayment(requestDto);
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
