package org.example.fanzip.payment.service;

import org.example.fanzip.payment.domain.Payments;
import org.example.fanzip.payment.domain.enums.PaymentsStatus;
import org.example.fanzip.payment.domain.enums.PaymentsType;
import org.example.fanzip.payment.dto.PaymentsResponseDto;
import org.example.fanzip.payment.repository.PaymentsRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class PaymentsApproveServiceTest {
    PaymentsRepository mockRepository = mock(PaymentsRepository.class);
    PaymentsRollbackService mockRollbackService = mock(PaymentsRollbackService.class);
    PaymentsApproveService approveService = new PaymentsApproveService(mockRepository, mockRollbackService);

    @Test
    @DisplayName("approvePaymentById - 성공")
    void testApprovePayment() {
        Payments payment = Payments.builder()
                .paymentId(1L)
                .paymentsType(PaymentsType.ORDER)
                .orderId(1L)
                .status(PaymentsStatus.PENDING)
                .amount(BigDecimal.valueOf(38000L))
                .build();

        when(mockRepository.findById(1L)).thenReturn(payment);
        doNothing().when(mockRepository).updateStatus(eq(1L), any());
        PaymentsResponseDto result = approveService.approvePaymentById(1L);
        assertEquals(PaymentsStatus.PAID, result.getStatus());
    }

    @Test
    @DisplayName("failedPaymentById - 성공")
    void testFailedPayment() {
        Payments payment = Payments.builder()
                .paymentId(2L)
                .status(PaymentsStatus.PENDING)
                .orderId(100L)
                .build();

        when(mockRepository.findById(2L)).thenReturn(payment);
        doNothing().when(mockRepository).updateStatus(eq(2L), any());
        doNothing().when(mockRollbackService).rollbackStock(payment);
        PaymentsResponseDto result = approveService.failedPaymentById(2L);
        assertEquals(PaymentsStatus.FAILED, result.getStatus());
    }

    @Test
    @DisplayName("cancelledPaymentById - 성공")
    void testCancelledPayment() {
        Payments payment = Payments.builder()
                .paymentId(3L)
                .status(PaymentsStatus.PENDING)
                .build();

        when(mockRepository.findById(3L)).thenReturn(payment);
        doNothing().when(mockRepository).updateStatus(eq(3L), any());
        PaymentsResponseDto result = approveService.cancelledPaymentById(3L);
        assertEquals(PaymentsStatus.CANCELLED, result.getStatus());
    }

    @Test
    @DisplayName("approvePaymentById - 금액 불일치 예외")
    void testApprovePaymentWithWrongAmount() {
        Payments payment = Payments.builder()
                .paymentId(4L)
                .paymentsType(PaymentsType.ORDER)
                .orderId(1L)
                .amount(BigDecimal.valueOf(10000L)) // 기대 금액과 다름
                .status(PaymentsStatus.PENDING)
                .build();

        when(mockRepository.findById(4L)).thenReturn(payment);
        assertThrows(IllegalArgumentException.class, () -> {
            approveService.approvePaymentById(4L);
        });
    }
}