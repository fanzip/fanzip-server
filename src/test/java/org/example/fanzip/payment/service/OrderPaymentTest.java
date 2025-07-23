package org.example.fanzip.payment.service;

import org.example.fanzip.payment.domain.Payments;
import org.example.fanzip.payment.domain.enums.PaymentStatus;
import org.example.fanzip.payment.domain.enums.PaymentType;
import org.example.fanzip.payment.dto.PaymentsRequestDto;
import org.example.fanzip.payment.dto.PaymentsResponseDto;
import org.example.fanzip.payment.repository.PaymentsRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class OrderPaymentTest {

    PaymentsRepository mockRepository = mock(PaymentsRepository.class);
    PaymentServiceImpl service = new PaymentServiceImpl(mockRepository);

    @Test
    @DisplayName("createPayment - success")
    void testCreatePaymentSuccess() { // 결제 생성 요청이 정상적으로 수행되는 경우
        PaymentsRequestDto dto = PaymentsRequestDto.builder()
                .transactionId("tx123")
                .paymentType(PaymentType.ORDER)
                .orderId(1L)
                .userId(1L)
                .build();
        when(mockRepository.existsByTransactionId("tx123")).thenReturn(false); // 트랜잭션 ID가 중복되지 않음
        doNothing().when(mockRepository).save(any(Payments.class));  // 저장 동작은 실제 실행 없이 무시
        PaymentsResponseDto result = service.createPayment(dto); // 서비스 호출
        assertNotNull(result);   // 응답이 null이 아님을 확인
    }

    @Test
    @DisplayName("approvePaymentById - success")
    void testApprovePayment() {        // 결제 승인 성공 케이스
        Payments payment = Payments.builder()
                .paymentId(1L)
                .paymentType(PaymentType.ORDER)
                .orderId(1L)
                .status(PaymentStatus.PENDING) // 초기 상태는 대기
                .build();

        when(mockRepository.findById(1L)).thenReturn(payment);
        doNothing().when(mockRepository).updateStatus(eq(1L), any());

        PaymentsResponseDto result = service.approvePaymentById(1L);

        // 상태가 PAID로 바뀌었는지 확인
        assertEquals(PaymentStatus.PAID, result.getStatus());
    }

    @Test
    @DisplayName("failedPaymentById - success")
    void testFailedPayment() {
        // 결제 실패 처리 케이스

        Payments payment = Payments.builder()
                .paymentId(2L)
                .paymentType(PaymentType.ORDER)
                .orderId(2L)
                .status(PaymentStatus.PENDING)
                .build();

        when(mockRepository.findById(2L)).thenReturn(payment);
        doNothing().when(mockRepository).updateStatus(eq(2L), any());

        PaymentsResponseDto result = service.failedPaymentById(2L);

        // 상태가 FAILED로 바뀌었는지 확인
        assertEquals(PaymentStatus.FAILED, result.getStatus());
    }

    @Test
    @DisplayName("cancelledPaymentById - success")
    void testCancelledPayment() {  // 결제 취소 처리 케이스
        Payments payment = Payments.builder()
                .paymentId(3L)
                .status(PaymentStatus.PENDING)
                .build();

        when(mockRepository.findById(3L)).thenReturn(payment);
        doNothing().when(mockRepository).updateStatus(eq(3L), any());
        PaymentsResponseDto result = service.cancelledPaymentById(3L);
        assertEquals(PaymentStatus.CANCELLED, result.getStatus()); // 상태가 CANCELLED로 바뀌었는지 확인
    }

    @Test
    @DisplayName("refundedPaymentById - success")
    void testRefundedPayment() { // 결제 환불 처리 케이스
        Payments payment = Payments.builder()
                .paymentId(4L)
                .status(PaymentStatus.PAID)
                .build();

        when(mockRepository.findById(4L)).thenReturn(payment);
        doNothing().when(mockRepository).updateStatus(eq(4L), any());
        PaymentsResponseDto result = service.refundedPaymentById(4L);
        assertEquals(PaymentStatus.REFUNDED, result.getStatus());  // 상태가 REFUNDED로 바뀌었는지 확인
    }

    @Test
    @DisplayName("getPayment - success")
    void testGetPayment() {  // 단건 결제 조회 케이스
        Payments payment = Payments.builder()
                .paymentId(5L)
                .status(PaymentStatus.PAID)
                .build();

        when(mockRepository.findById(5L)).thenReturn(payment);
        PaymentsResponseDto result = service.getPayment(5L);
        assertEquals(PaymentStatus.PAID, result.getStatus());  // 상태가 정확히 조회되었는지 확인
    }

    @Test
    @DisplayName("getMyPayments - success")
    void testGetMyPayments() {  // 유저 ID로 본인의 모든 결제 내역 조회 케이스
        Payments p1 = Payments.builder().userId(1L).build();
        Payments p2 = Payments.builder().userId(1L).build();

        when(mockRepository.findByUserId(1L)).thenReturn(Arrays.asList(p1, p2));
        var result = service.getMyPayments(1L);
        assertEquals(2, result.size()); // 2건이 잘 조회되었는지 확인
    }
}