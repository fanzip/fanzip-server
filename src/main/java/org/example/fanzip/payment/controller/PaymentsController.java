package org.example.fanzip.payment.controller;

import lombok.RequiredArgsConstructor;
import org.example.fanzip.payment.dto.PaymentsRequestDto;
import org.example.fanzip.payment.dto.PaymentsResponseDto;
import org.example.fanzip.payment.service.PaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentsController {
    private final PaymentService paymentService;

    @PostMapping("/request")
    public ResponseEntity<PaymentsResponseDto> createPayment(@RequestBody PaymentsRequestDto requestDto){
        PaymentsResponseDto responseDto = paymentService.createPayment(requestDto);
        return ResponseEntity.ok(responseDto);
    }
    @PostMapping("/{paymentId}/approve")
    public ResponseEntity<PaymentsResponseDto> approvePayment(@PathVariable Long paymentId){
        PaymentsResponseDto responseDto = paymentService.approvePaymentById(paymentId);
        return ResponseEntity.ok(responseDto);
    }
}
