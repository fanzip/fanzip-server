package org.example.fanzip.payment.controller;

import lombok.RequiredArgsConstructor;
import org.example.fanzip.payment.dto.PaymentsRequestDto;
import org.example.fanzip.payment.dto.PaymentsResponseDto;
import org.example.fanzip.payment.service.PaymentsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentsController {
    private final PaymentsService paymentsService;

    @PostMapping("/request")
    public ResponseEntity<PaymentsResponseDto> createPayment(@RequestBody PaymentsRequestDto requestDto){
        PaymentsResponseDto responseDto = paymentsService.createPayment(requestDto);
        return ResponseEntity.ok(responseDto);
    }
    @PostMapping("/{paymentId}/approve")
    public ResponseEntity<PaymentsResponseDto> approvePayment(@PathVariable Long paymentId){
        PaymentsResponseDto responseDto = paymentsService.approvePaymentById(paymentId);
        return ResponseEntity.ok(responseDto);
    }
    @PostMapping("/{paymentId}/fail")
    public ResponseEntity<PaymentsResponseDto> failedPayment(@PathVariable Long paymentId){
        PaymentsResponseDto responseDto = paymentsService.failedPaymentById(paymentId);
        return ResponseEntity.ok(responseDto);
    }
    @PostMapping("/{paymentId}/cancelled")
    public ResponseEntity<PaymentsResponseDto> cancelled(@PathVariable Long paymentId){
        PaymentsResponseDto responseDto= paymentsService.cancelledPaymentById(paymentId);
        return ResponseEntity.ok(responseDto);
    }
    @PostMapping("/{paymentId}/refunded")
    public ResponseEntity<PaymentsResponseDto> refunded(@PathVariable Long paymentId){
        PaymentsResponseDto responseDto = paymentsService.refundedPaymentById(paymentId);
        return ResponseEntity.ok(responseDto);
    }
    @GetMapping("/payment/{paymentId}")
    public ResponseEntity<PaymentsResponseDto> getPaymentDetail(@PathVariable Long paymentId){
        PaymentsResponseDto responseDto = paymentsService.getPayment(paymentId);
        return ResponseEntity.ok(responseDto);
    }
    @GetMapping("/user/{userId}") // 로그인 인증 구현 후 변경 예정
    public ResponseEntity<List<PaymentsResponseDto>> getMyPaymentDetail(@PathVariable Long userId){
        List<PaymentsResponseDto> responseDtoList = paymentsService.getMyPayments(userId);
        return ResponseEntity.ok(responseDtoList);
    }
}
