package org.example.fanzip.payment.service;

import org.example.fanzip.payment.domain.Payments;
import org.example.fanzip.payment.domain.enums.PaymentsType;
import org.example.fanzip.payment.dto.PaymentsRequestDto;
import org.example.fanzip.payment.dto.PaymentsResponseDto;
import org.example.fanzip.payment.repository.PaymentsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;

class PaymentCreationServiceTest {

    private PaymentsRepository paymentsRepository;
    private PaymentCreationService paymentCreationService;

    @BeforeEach
    void setUp() {
        paymentsRepository = mock(PaymentsRepository.class);
        paymentCreationService = new PaymentCreationService(paymentsRepository);
    }

    @Test
    @DisplayName("정상 결제 생성")
    void createPayment_success() {
        System.out.println("🎯 테스트 시작: 정상 결제 생성");

        PaymentsRequestDto dto = createMockDto(PaymentsType.ORDER, 1L, null, null);
        when(paymentsRepository.existsByTransactionId(dto.getTransactionId())).thenReturn(false);
        doNothing().when(paymentsRepository).save(any(Payments.class));

        PaymentsResponseDto result = paymentCreationService.createPayment(dto);

        System.out.println("✅ 정상 결제 성공: " + result);
        assertThat(result).isNotNull();
        verify(paymentsRepository).save(any(Payments.class));

        System.out.println("✅ 테스트 완료: 정상 결제 생성\n");
    }

    @Test
    @DisplayName("중복 transactionId로 인한 예외")
    void createPayment_duplicateTransactionId() {
        System.out.println("🎯 테스트 시작: 중복 transactionId 예외");

        PaymentsRequestDto dto = createMockDto(PaymentsType.ORDER, 1L, null, null);
        when(paymentsRepository.existsByTransactionId(dto.getTransactionId())).thenReturn(true);

        assertThatThrownBy(() -> paymentCreationService.createPayment(dto))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("이미 처리된 결제");

        System.out.println("✅ 테스트 완료: 중복 transactionId 예외 정상 발생\n");
    }

    @Test
    @DisplayName("외래키 2개 이상 설정 시 예외 발생")
    void createPayment_multipleForeignKeys() {
        System.out.println("🎯 테스트 시작: 외래키 2개 이상 설정 예외");

        PaymentsRequestDto dto = createMockDto(PaymentsType.ORDER, 1L, 2L, null);

        assertThatThrownBy(() -> paymentCreationService.createPayment(dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("정확히 하나만 존재해야");

        System.out.println("✅ 테스트 완료: 외래키 2개 이상 설정 예외 정상 발생\n");
    }

    @Test
    @DisplayName("멤버십 이미 구독 중일 경우 예외")
    void createPayment_alreadySubscribed() {
        System.out.println("🎯 테스트 시작: 멤버십 중복 구독 예외");

        PaymentsRequestDto dto = createMockDto(PaymentsType.MEMBERSHIP, null, null, 1L);
        when(paymentsRepository.existsByTransactionId(dto.getTransactionId())).thenReturn(false);
        when(paymentsRepository.existsMembershipPayment(dto.getUserId(), dto.getMembershipId())).thenReturn(true);

        assertThatThrownBy(() -> paymentCreationService.createPayment(dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("이미 구독 중인 멤버십");

        System.out.println("✅ 테스트 완료: 멤버십 중복 구독 예외 정상 발생\n");
    }

    @Test
    @DisplayName("상품 재고가 없을 때 예외")
    void createPayment_noStock() {
        System.out.println("🎯 테스트 시작: 상품 재고 부족 예외");

        PaymentsRequestDto dto = createMockDto(PaymentsType.ORDER, 1L, null, null);
        when(paymentsRepository.existsByTransactionId(dto.getTransactionId())).thenReturn(false);

        PaymentCreationService service = new PaymentCreationService(paymentsRepository) {
            protected void validateStockAvailability(Long orderId, Long reservationId, Long membershipId) {
                throw new IllegalStateException("상품 재고가 부족합니다");
            }
        };

        assertThatThrownBy(() -> service.createPayment(dto))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("상품 재고가 부족합니다");

        System.out.println("✅ 테스트 완료: 상품 재고 부족 예외 정상 발생\n");
    }

    @Test
    @DisplayName("좌석이 없을 때 예외")
    void createPayment_noSeats() {
        System.out.println("🎯 테스트 시작: 예약 좌석 부족 예외");

        PaymentsRequestDto dto = createMockDto(PaymentsType.RESERVATION, null, 1L, null);
        when(paymentsRepository.existsByTransactionId(dto.getTransactionId())).thenReturn(false);

        PaymentCreationService service = new PaymentCreationService(paymentsRepository) {
            protected void validateStockAvailability(Long orderId, Long reservationId, Long membershipId) {
                throw new IllegalStateException("예약 가능한 인원이 없습니다");
            }
        };

        assertThatThrownBy(() -> service.createPayment(dto))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("예약 가능한 인원이 없습니다");

        System.out.println("✅ 테스트 완료: 예약 좌석 부족 예외 정상 발생\n");
    }

    @Test
    @DisplayName("지원하지 않는 결제 타입인 경우 예외")
    void createPayment_invalidType() {
        System.out.println("🎯 테스트 시작: 결제 타입 null 예외");

        PaymentsRequestDto dto = createMockDto(null, 1L, null, null);
        when(paymentsRepository.existsByTransactionId(dto.getTransactionId())).thenReturn(false);

        assertThatThrownBy(() -> paymentCreationService.createPayment(dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("결제 유형이 존재하지 않습니다.");

        System.out.println("✅ 테스트 완료: 결제 타입 null 예외 정상 발생\n");
    }

    // 공통 DTO 생성 메서드
    private PaymentsRequestDto createMockDto(PaymentsType type, Long orderId, Long reservationId, Long membershipId) {
        return PaymentsRequestDto.builder()
                .transactionId("txn-1234")
                .userId(10L)
                .paymentsType(type)
                .orderId(orderId)
                .reservationId(reservationId)
                .membershipId(membershipId)
                .amount(BigDecimal.valueOf(100000))
                .build();
    }
}