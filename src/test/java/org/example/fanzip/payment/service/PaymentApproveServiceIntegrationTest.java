package org.example.fanzip.payment.service;

import org.example.fanzip.config.RootConfig;
import org.example.fanzip.payment.domain.Payments;
import org.example.fanzip.payment.domain.enums.PaymentMethod;
import org.example.fanzip.payment.domain.enums.PaymentStatus;
import org.example.fanzip.payment.domain.enums.PaymentType;
import org.example.fanzip.payment.dto.PaymentResponseDto;
import org.example.fanzip.payment.repository.PaymentRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = RootConfig.class)
@Transactional
@Rollback
class PaymentApproveServiceIntegrationTest {

    @Autowired
    private PaymentApproveService paymentApproveService;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private PaymentRollbackService paymentRollbackService;

    @Test
    @DisplayName("✅ 통합 테스트: 결제 승인 성공")
    void testApprovePayment_success() {
        Payments payment = Payments.builder()
                .userId(51111L)
                .paymentType(PaymentType.MEMBERSHIP)
                .membershipId(1L)
                .transactionId("abcdffffff")
                .paymentMethod(PaymentMethod.KAKAOPAY)
                .status(PaymentStatus.PENDING)
                .amount(new BigDecimal("10000"))
                .build();
        paymentRepository.save(payment);

        PaymentResponseDto result = paymentApproveService.approvePaymentById(payment.getPaymentId());
        assertThat(result.getStatus()).isEqualTo(PaymentStatus.PAID);
    }
    @Test
    @DisplayName("✅ 통합 테스트: 결제 실패 처리")
    void testFailedPayment() {
        Payments payment = Payments.builder()
                .userId(51111L)
                .paymentType(PaymentType.ORDER)
                .orderId(1L)
                .transactionId("abcdffffff")
                .paymentMethod(PaymentMethod.KAKAOPAY)
                .status(PaymentStatus.PENDING)
                .amount(new BigDecimal("38000"))
                .build();

        paymentRepository.save(payment);
        System.out.println("paymentId = " + payment.getPaymentId());

        PaymentResponseDto result = paymentApproveService.failedPaymentById(payment.getPaymentId());
        assertThat(result.getStatus()).isEqualTo(PaymentStatus.FAILED);
    }
    @Test
    @DisplayName("✅ 통합 테스트: 결제 취소 처리")
    void testCancelledPayment() {
        Payments payment = Payments.builder()
                .userId(51111L)
                .paymentType(PaymentType.ORDER)
                .orderId(1L)
                .transactionId("abcdffffff")
                .paymentMethod(PaymentMethod.KAKAOPAY)
                .status(PaymentStatus.PENDING)
                .amount(new BigDecimal("38000"))
                .build();

        paymentRepository.save(payment);
        System.out.println("paymentId = " + payment.getPaymentId());

        PaymentResponseDto result = paymentApproveService.cancelledPaymentById(payment.getPaymentId());
        assertThat(result.getStatus()).isEqualTo(PaymentStatus.CANCELLED);
    }
    @Test
    @DisplayName("✅ 통합 테스트: 결제 롤백 처리")
    void testRollbackPayment() {
        Payments payment = Payments.builder()
                .userId(51111L)
                .paymentType(PaymentType.ORDER)
                .orderId(1L)
                .transactionId("abcdffffff")
                .paymentMethod(PaymentMethod.KAKAOPAY)
                .status(PaymentStatus.PAID)
                .amount(new BigDecimal("38000"))
                .build();

        paymentRepository.save(payment);
        System.out.println("paymentId = " + payment.getPaymentId());

        PaymentResponseDto result = paymentRollbackService.refundedPaymentById(payment.getPaymentId());

        assertThat(result.getStatus()).isEqualTo(PaymentStatus.REFUNDED);
    }
    @Test
    @DisplayName("✅ 통합 테스트: 이미 승인된 결제건은 재승인 ❌")
    void testAlreadyApprovedPayment() {
        Payments payment = Payments.builder()
                .userId(51111L)
                .paymentType(PaymentType.ORDER)
                .orderId(1L)
                .transactionId("abcdffffff")
                .paymentMethod(PaymentMethod.KAKAOPAY)
                .status(PaymentStatus.PAID)
                .amount(new BigDecimal("38000"))
                .build();
        paymentRepository.save(payment);

        assertThatThrownBy(() ->
                paymentApproveService.approvePaymentById(payment.getPaymentId())
        ).isInstanceOf(IllegalStateException.class);
    }
    @Test
    @DisplayName("✅ 통합 테스트: 금액 불일치로 결제 승인 실패")
    void testPaymentAmountMismatch() {
        Payments payment = Payments.builder()
                .userId(1L)
                .paymentType(PaymentType.ORDER)
                .orderId(1L)
                .transactionId("txn-123")
                .paymentMethod(PaymentMethod.KAKAOPAY)
                .status(PaymentStatus.PENDING)
                .amount(new BigDecimal("99999")) // 일부러 잘못된 금액
                .build();
        paymentRepository.save(payment);

        assertThatThrownBy(() ->
                paymentApproveService.approvePaymentById(payment.getPaymentId())
        ).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("결제 요청 금액이 실제 금액과 일치하지 않습니다.");
    }
    @Test
    @DisplayName("✅ 통합 테스트: 존재하지 않는 결제 ID로 승인 요청 시 예외 발생")
    void testApprovePayment_notFound() {
        assertThatThrownBy(() ->
                paymentApproveService.approvePaymentById(99999L)
        ).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("해당 결제 정보를 찾을 수 없습니다");
    }
    @Test
    @DisplayName("✅ 통합 테스트: 예외 발생 시 결제 상태 롤백 확인")
    void testRollbackOnException() {
        Payments payment = Payments.builder()
                .userId(1L)
                .paymentType(PaymentType.ORDER)
                .orderId(1L)
                .transactionId("txn-123")
                .paymentMethod(PaymentMethod.KAKAOPAY)
                .status(PaymentStatus.PENDING)
                .amount(new BigDecimal("99999")) // 잘못된 금액
                .build();
        paymentRepository.save(payment);

        assertThatThrownBy(() -> {
            paymentApproveService.approvePaymentById(payment.getPaymentId());
            throw new RuntimeException("강제 실패");
        }).isInstanceOf(RuntimeException.class);

        Payments result = paymentRepository.findById(payment.getPaymentId());
        assertThat(result.getStatus()).isEqualTo(PaymentStatus.PENDING);
    }
    @Test
    @DisplayName("✅ 통합 테스트: PENDING이 아닌 상태에서 결제 실패 처리 시 예외 발생")
    void testFailNonPendingPayment() {
        Payments payment = Payments.builder()
                .userId(51111L)
                .paymentType(PaymentType.ORDER)
                .orderId(1L)
                .transactionId("test-txn")
                .paymentMethod(PaymentMethod.KAKAOPAY)
                .status(PaymentStatus.PAID) // 이미 승인된 상태
                .amount(new BigDecimal("12000"))
                .build();
        paymentRepository.save(payment);

        assertThatThrownBy(() ->
                paymentApproveService.failedPaymentById(payment.getPaymentId())
        ).isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("PENDING이 아닌 상태에서 실패 처리할 수 없습니다.");
    }
}