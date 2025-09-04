package org.example.fanzip.payment.controller;

import io.swagger.annotations.*;
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

@Api(tags = "Payment", description = "결제 관리 API")
@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@Slf4j
public class PaymentController {
    private final PaymentService paymentService;

    @ApiOperation(value = "결제 요청 생성", notes = "새로운 결제 요청을 생성합니다.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "결제 요청 생성 성공"),
            @ApiResponse(code = 400, message = "잘못된 요청 데이터"),
            @ApiResponse(code = 500, message = "서버 오류")
    })
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
    @ApiOperation(value = "결제 승인", notes = "결제를 승인 상태로 변경합니다.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "결제 승인 성공"),
            @ApiResponse(code = 404, message = "결제 내역을 찾을 수 없음"),
            @ApiResponse(code = 500, message = "서버 오류")
    })
    @PatchMapping("/{paymentId}/approve")
    public ResponseEntity<PaymentResponseDto> approvePayment(
            @ApiParam(value = "결제 ID", required = true, example = "1")
            @PathVariable Long paymentId){
        PaymentResponseDto responseDto = paymentService.approvePaymentById(paymentId);
        return ResponseEntity.ok(responseDto);
    }
    @ApiOperation(value = "결제 실패 처리", notes = "결제를 실패 상태로 변경합니다.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "결제 실패 처리 성공"),
            @ApiResponse(code = 404, message = "결제 내역을 찾을 수 없음"),
            @ApiResponse(code = 500, message = "서버 오류")
    })
    @PatchMapping("/{paymentId}/fail")
    public ResponseEntity<PaymentResponseDto> failedPayment(
            @ApiParam(value = "결제 ID", required = true, example = "1")
            @PathVariable Long paymentId){
        PaymentResponseDto responseDto = paymentService.failedPaymentById(paymentId);
        return ResponseEntity.ok(responseDto);
    }
    @ApiOperation(value = "결제 취소", notes = "결제를 취소 상태로 변경합니다.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "결제 취소 성공"),
            @ApiResponse(code = 404, message = "결제 내역을 찾을 수 없음"),
            @ApiResponse(code = 500, message = "서버 오류")
    })
    @PatchMapping("/{paymentId}/cancelled")
    public ResponseEntity<PaymentResponseDto> cancelled(
            @ApiParam(value = "결제 ID", required = true, example = "1")
            @PathVariable Long paymentId){
        PaymentResponseDto responseDto= paymentService.cancelledPaymentById(paymentId);
        return ResponseEntity.ok(responseDto);
    }
    @ApiOperation(value = "결제 환불", notes = "결제를 환불 상태로 변경합니다.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "결제 환불 성공"),
            @ApiResponse(code = 404, message = "결제 내역을 찾을 수 없음"),
            @ApiResponse(code = 500, message = "서버 오류")
    })
    @PatchMapping("/{paymentId}/refunded")
    public ResponseEntity<PaymentResponseDto> refunded(
            @ApiParam(value = "결제 ID", required = true, example = "1")
            @PathVariable Long paymentId){
        PaymentResponseDto responseDto = paymentService.refundedPaymentById(paymentId);
        return ResponseEntity.ok(responseDto);
    }
    @ApiOperation(value = "결제 상세 정보 조회", notes = "특정 결제의 상세 정보를 조회합니다.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "결제 상세 정보 조회 성공"),
            @ApiResponse(code = 404, message = "결제 내역을 찾을 수 없음"),
            @ApiResponse(code = 500, message = "서버 오류")
    })
    @GetMapping("/{paymentId}")
    public ResponseEntity<PaymentResponseDto> getPaymentDetail(
            @ApiParam(value = "결제 ID", required = true, example = "1")
            @PathVariable Long paymentId){
        PaymentResponseDto responseDto = paymentService.getPayment(paymentId);
        return ResponseEntity.ok(responseDto);
    }
    @ApiOperation(value = "내 결제 내역 조회", notes = "사용자의 모든 결제 내역을 조회합니다. (향후 로그인 인증으로 변경 예정)")
    @ApiResponses({
            @ApiResponse(code = 200, message = "내 결제 내역 조회 성공"),
            @ApiResponse(code = 404, message = "사용자를 찾을 수 없음"),
            @ApiResponse(code = 500, message = "서버 오류")
    })
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<PaymentResponseDto>> getMyPaymentDetail(
            @ApiParam(value = "사용자 ID", required = true, example = "1")
            @PathVariable Long userId){
        List<PaymentResponseDto> responseDtoList = paymentService.getMyPayments(userId);
        return ResponseEntity.ok(responseDtoList);
    }
}
