package org.example.fanzip.payment.service;

import org.example.fanzip.meeting.mapper.FanMeetingReservationMapper;
import org.example.fanzip.meeting.mapper.FanMeetingSeatMapper;
import org.example.fanzip.membership.mapper.MembershipMapper;
import org.example.fanzip.fancard.mapper.FancardMapper;
import org.example.fanzip.fancard.service.FancardService;
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
    PaymentValidator mockValidator = mock(PaymentValidator.class);
    FanMeetingReservationMapper mockReservationMapper = mock(FanMeetingReservationMapper.class);
    FanMeetingSeatMapper mockSeatMapper = mock(FanMeetingSeatMapper.class);
    MembershipMapper mockMembershipMapper = mock(MembershipMapper.class);
    FancardMapper mockFancardMapper = mock(FancardMapper.class);
    FancardService mockFancardService = mock(FancardService.class);
    
    PaymentApproveService approveService = new PaymentApproveService(
        mockRepository, 
        mockRollbackService, 
        mockValidator,
        mockReservationMapper,
        mockSeatMapper,
        mockMembershipMapper,
        mockFancardMapper,
        mockFancardService
    );

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

        assertThrows(RuntimeException.class, () -> {
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

        assertThrows(RuntimeException.class, () -> {
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

        assertThrows(RuntimeException.class, () -> {
            approveService.cancelledPaymentById(12L);
        });
        System.out.println("✅ 테스트 완료: 이미 취소된 결제 예외 정상 발생\n");
    }
    @Test
    @DisplayName("approvePaymentById - MEMBERSHIP 타입 getExpectedAmount 미구현으로 예외")
    void testApprovePaymentWithWrongAmount() {
        System.out.println("🎯 테스트 시작: MEMBERSHIP 타입 금액 검증 미구현으로 예외 발생");

        Payments payment = Payments.builder()
                .paymentId(4L)
                .paymentType(PaymentType.MEMBERSHIP)
                .membershipId(1L)
                .amount(BigDecimal.valueOf(10000L))
                .status(PaymentStatus.PENDING)
                .build();

        when(mockRepository.findById(4L)).thenReturn(payment);
        // getExpectedAmount에서 MEMBERSHIP 타입 처리가 없어서 UNSUPPORTED_PAYMENT_TYPE 예외 발생
        assertThrows(RuntimeException.class, () -> {
            approveService.approvePaymentById(4L);
        });
        System.out.println("✅ 테스트 완료: MEMBERSHIP 타입 미지원 예외 정상 발생\n");
    }
    @Test
    @DisplayName("approvePaymentById - 존재하지 않는 결제 ID")
    void testApprovePaymentNotFound() {
        System.out.println("🎯 테스트 시작: 존재하지 않는 결제 ID");

        when(mockRepository.findById(999L)).thenReturn(null);

        assertThrows(RuntimeException.class, () -> {
            approveService.approvePaymentById(999L);
        });
        System.out.println("✅ 테스트 완료: 결제 ID 없음 예외 정상 발생\n");
    }

    @DisplayName("동시 결제 승인 - 각각 다른 ID로 정상 처리")
    @Test
    void testApproveReservation_concurrentDifferentPayments() throws InterruptedException {
        System.out.println("🎯 테스트 시작: 서로 다른 결제 ID로 동시 승인 - 모두 성공");

        List<Payments> paymentsList = IntStream.range(0, 5)
                .mapToObj(i -> Payments.builder()
                        .paymentId((long) i)
                        .status(PaymentStatus.PENDING)
                        .orderId((long) (1000 + i)) // 각각 다른 주문 ID
                        .amount(BigDecimal.valueOf(12000))
                        .paymentType(PaymentType.ORDER) // ORDER 타입은 현재 검증 로직이 없어서 통과
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
        ExecutorService executor = Executors.newFixedThreadPool(5);
        List<Future<Boolean>> results = new ArrayList<>();

        for (Payments p : paymentsList) {
            results.add(executor.submit(() -> {
                try {
                    System.out.println("[시도] 결제 승인 요청 - paymentId: " + p.getPaymentId());
                    approveService.approvePaymentById(p.getPaymentId());
                    System.out.println("✅ 결제 승인 성공 - paymentId: " + p.getPaymentId());
                    return true;
                } catch (Exception e) {
                    System.out.println("❌ 결제 승인 실패 - paymentId: " + p.getPaymentId() + " / 이유: " + e.getMessage());
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

        System.out.println("[요약] 결제 승인 성공 수: " + successCount);
        assertEquals(5, successCount, "모든 결제가 성공해야 함");
        System.out.println("✅ 테스트 완료: 동시 결제 승인 검증 완료\n");
    }
}