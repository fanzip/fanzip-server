package org.example.fanzip.payment.controller;

import lombok.RequiredArgsConstructor;
import org.example.fanzip.payment.dto.PaymentsDto;
import org.example.fanzip.payment.service.PaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentsController {
    private final PaymentService paymentService;

    @PostMapping("")
    public ResponseEntity<PaymentsDto> createPayment(@RequestBody PaymentsDto paymentsDto){
        PaymentsDto created = paymentService.createdPayment(paymentsDto);
        return ResponseEntity.ok(created);
    }
}
