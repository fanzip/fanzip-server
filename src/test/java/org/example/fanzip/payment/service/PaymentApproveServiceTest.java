package org.example.fanzip.payment.service;

import org.example.fanzip.payment.domain.Payments;
import org.example.fanzip.payment.domain.enums.PaymentStatus;
import org.example.fanzip.payment.domain.enums.PaymentType;
import org.example.fanzip.payment.dto.PaymentResponseDto;
import org.example.fanzip.payment.repository.PaymentRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.*;
import java.util.stream.IntStream;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class PaymentApproveServiceTest {
    PaymentRepository mockRepository = mock(PaymentRepository.class);
    PaymentRollbackService mockRollbackService = mock(PaymentRollbackService.class);
    PaymentApproveService approveService = new PaymentApproveService(mockRepository, mockRollbackService);
    PaymentRepository paymentRepository;

    @Test
    @DisplayName("approvePaymentById - 성공")
    void testApprovePayment() {
        System.out.println("🎯 테스트 시작: 결제 승인 성공");

        Payments payment = Payments.builder()
                .paymentId(1L)
                .paymentType(PaymentType.ORDER)
                .orderId(1L)
                .status(PaymentStatus.PENDING)
                .amount(BigDecimal.valueOf(38000L))
                .build();

        when(mockRepository.findById(1L)).thenReturn(payment);
        doNothing().when(mockRepository).updateStatus(payment);
        PaymentResponseDto result = approveService.approvePaymentById(1L);
        assertEquals(PaymentStatus.PAID, result.getStatus());

        System.out.println("✅ 테스트 완료: 결제 승인 성공\n");
    }
    @Test
    @DisplayName("approvePaymentById - 이미 PAID 상태인 경우 예외")
    void testApproveAlreadyPaidPayment() {
        System.out.println("🎯 테스트 시작: 이미 결제 완료된 건 예외 발생");

        Payments payment = Payments.builder()
                .paymentId(10L)
                .status(PaymentStatus.PAID)
                .paymentType(PaymentType.ORDER)
                .orderId(1L)
                .amount(BigDecimal.valueOf(38000L))
                .build();

        when(mockRepository.findById(10L)).thenReturn(payment);

        assertThrows(IllegalStateException.class, () -> {
            approveService.approvePaymentById(10L);
        });
        System.out.println("✅ 테스트 완료: 이미 결제된 건 예외 정상 발생\n");
    }
    @Test
    @DisplayName("failedPaymentById - 성공")
    void testFailedPayment() {
        System.out.println("🎯 테스트 시작: 결제 실패 처리");

        Payments payment = Payments.builder()
                .paymentId(2L)
                .status(PaymentStatus.PENDING)
                .orderId(100L)
                .build();

        when(mockRepository.findById(2L)).thenReturn(payment);
        doNothing().when(mockRepository).updateStatus(payment);
        doNothing().when(mockRollbackService).rollbackStock(payment);
        PaymentResponseDto result = approveService.failedPaymentById(2L);
        assertEquals(PaymentStatus.FAILED, result.getStatus());

        System.out.println("✅ 테스트 완료: 결제 실패 처리 성공\n");
    }
    @Test
    @DisplayName("failPaymentById - 이미 FAILED 상태에서 호출 시 예외")
    void testFailAlreadyFailedPayment() {
        System.out.println("🎯 테스트 시작: 이미 실패 처리된 건 예외 발생");

        Payments payment = Payments.builder()
                .paymentId(11L)
                .status(PaymentStatus.FAILED)
                .build();

        when(mockRepository.findById(11L)).thenReturn(payment);

        assertThrows(IllegalStateException.class, () -> {
            approveService.failedPaymentById(11L);
        });
        System.out.println("✅ 테스트 완료: 이미 실패된 결제 예외 정상 발생\n");
    }
    @Test
    @DisplayName("cancelledPaymentById - 성공")
    void testCancelledPayment() {
        System.out.println("🎯 테스트 시작: 결제 취소 처리");

        Payments payment = Payments.builder()
                .paymentId(3L)
                .status(PaymentStatus.PENDING)
                .build();

        when(mockRepository.findById(3L)).thenReturn(payment);
        doNothing().when(mockRepository).updateStatus(payment);
        PaymentResponseDto result = approveService.cancelledPaymentById(3L);
        assertEquals(PaymentStatus.CANCELLED, result.getStatus());

        System.out.println("✅ 테스트 완료: 결제 취소 처리 성공\n");
    }
    @Test
    @DisplayName("cancelledPaymentById - 이미 CANCELLED 상태에서 호출 시 예외")
    void testCancelAlreadyCancelledPayment() {
        System.out.println("🎯 테스트 시작: 이미 취소된 건 예외 발생");

        Payments payment = Payments.builder()
                .paymentId(12L)
                .status(PaymentStatus.CANCELLED)
                .build();

        when(mockRepository.findById(12L)).thenReturn(payment);

        assertThrows(IllegalStateException.class, () -> {
            approveService.cancelledPaymentById(12L);
        });
        System.out.println("✅ 테스트 완료: 이미 취소된 결제 예외 정상 발생\n");
    }
    @Test
    @DisplayName("approvePaymentById - 금액 불일치 예외")
    void testApprovePaymentWithWrongAmount() {
        System.out.println("🎯 테스트 시작: 금액 불일치 예외 발생");

        Payments payment = Payments.builder()
                .paymentId(4L)
                .paymentType(PaymentType.ORDER)
                .orderId(1L)
                .amount(BigDecimal.valueOf(10000L)) // 기대 금액과 다름
                .status(PaymentStatus.PENDING)
                .build();

        when(mockRepository.findById(4L)).thenReturn(payment);
        assertThrows(IllegalArgumentException.class, () -> {
            approveService.approvePaymentById(4L);
        });
        System.out.println("✅ 테스트 완료: 금액 불일치 예외 정상 발생\n");
    }
    @Test
    @DisplayName("approvePaymentById - 존재하지 않는 결제 ID")
    void testApprovePaymentNotFound() {
        System.out.println("🎯 테스트 시작: 존재하지 않는 결제 ID");

        when(mockRepository.findById(999L)).thenReturn(null);

        assertThrows(IllegalArgumentException.class, () -> {
            approveService.approvePaymentById(999L);
        });
        System.out.println("✅ 테스트 완료: 결제 ID 없음 예외 정상 발생\n");
    }

    @DisplayName("동일한 seatId 결제 승인 - 최대 1건만 성공")
    @Test
    void testApproveReservation_concurrentSameSeat() throws InterruptedException {
        System.out.println("🎯 테스트 시작: 동일 좌석 ID 동시 결제 - 최대 1건만 성공");

        Long sharedSeatId = 200L;
        Set<Long> reservedSeats = ConcurrentHashMap.newKeySet(); // 중복 방지용

        PaymentApproveService service = new PaymentApproveService(mockRepository, mockRollbackService) {
            @Override
            protected void validateStockAvailability(Long orderId, Long reservationId, Long membershipId) {
                if (reservationId != null) {   // 좌석 ID로 중복 확인
                    boolean alreadyReserved = !reservedSeats.add(reservationId);
                    System.out.println("[검사] " + Thread.currentThread().getName()
                            + " - reservationId: " + reservationId
                            + " / alreadyReserved: " + alreadyReserved);
                    if (alreadyReserved) {
                        throw new IllegalStateException("이미 예약된 좌석입니다");
                    }
                }
            }
            protected BigDecimal getExpectedAmountMock(Payments payments) {
                return BigDecimal.valueOf(12000); // 예매 금액 mock
            }
        };

        List<Payments> paymentsList = IntStream.range(0, 10)
                .mapToObj(i -> Payments.builder()
                        .paymentId((long) i)
                        .status(PaymentStatus.PENDING)
                        .reservationId(sharedSeatId) // 같은 좌석 ID로 가정
                        .amount(BigDecimal.valueOf(12000))
                        .paymentType(PaymentType.RESERVATION)
                        .build())
                .toList();

        for (Payments p : paymentsList) {
            when(mockRepository.findById(p.getPaymentId())).thenReturn(p);
        }

        doAnswer(invocation -> {
            Payments payments = invocation.getArgument(0);

            System.out.println("[DB 업데이트] paymentId = " + payments.getPaymentId() + " → 상태: PAID");
            return null;
        }).when(mockRepository).updateStatus(any(Payments.class));

        // 실행
        ExecutorService executor = Executors.newFixedThreadPool(10);
        List<Future<Boolean>> results = new ArrayList<>();

        for (Payments p : paymentsList) {
            results.add(executor.submit(() -> {
                try {
                    System.out.println("[시도] 결제 승인 요청 - paymentId: " + p.getPaymentId());
                    service.approvePaymentById(p.getPaymentId());
                    System.out.println("✅ 좌석 예약 성공 - paymentId: " + p.getPaymentId());
                    return true;
                } catch (Exception e) {
                    System.out.println("❌ 좌석 예약 실패 - paymentId: " + p.getPaymentId() + " / 이유: " + e.getMessage());
                    return false;
                }
            }));
        }

        executor.shutdown();
        executor.awaitTermination(5, TimeUnit.SECONDS);

        long successCount = results.stream()
                .filter(future -> {
                    try {
                        return future.get();
                    } catch (Exception e) {
                        return false;
                    }
                })
                .count();

        System.out.println("[요약] 좌석 예약 성공 수: " + successCount);
        assertEquals(1, successCount); // 같은 좌석은 한 명만 성공해야 함
        System.out.println("✅ 테스트 완료: 동일 좌석 결제 성공 수 검증 완료\n");
    }
}