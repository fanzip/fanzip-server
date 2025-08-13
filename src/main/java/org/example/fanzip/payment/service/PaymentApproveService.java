package org.example.fanzip.payment.service;

import lombok.RequiredArgsConstructor;
import org.example.fanzip.global.exception.BusinessException;
import org.example.fanzip.global.exception.payment.PaymentErrorCode;
import org.example.fanzip.market.mapper.MarketOrderMapper;
import org.example.fanzip.payment.domain.Payments;
import org.example.fanzip.payment.domain.enums.PaymentStatus;
import org.example.fanzip.payment.dto.PaymentResponseDto;
import org.example.fanzip.payment.repository.PaymentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PaymentApproveService {
    private final PaymentRepository paymentRepository;
    private final PaymentRollbackService paymentRollbackService;
    private final PaymentValidator paymentValidator;
//    private final MarketOrderMapper marketOrderMapper;

    @Transactional
    public PaymentResponseDto approvePaymentById(Long paymentId) {
        Payments payments = paymentRepository.findById(paymentId);
        if (payments == null) {
            throw new BusinessException(PaymentErrorCode.PAYMENT_NOT_FOUND);
        }
        paymentValidator.validateStockAvailability(payments.getOrderId(), payments.getReservationId(), payments.getMembershipId()); // 결제 승인 시 재고 수량 검사 홤수
        // TODO : 주문 금액과 결제 요청 금액이 맞는지 로직 변경 필요
        BigDecimal expectedAmount = getExpectedAmountMock(payments);
        if (payments.getAmount().compareTo(expectedAmount) != 0)  {
            throw new BusinessException(PaymentErrorCode.AMOUNT_MISMATCH);
        }

        // 주문 금액으로 검증 (ORDERS)
//        BigDecimal expectedAmount;
//        if (payments.getOrderId() != null) {
//            Map<String, Object> row = marketOrderMapper.selectOrderForPayment(payments.getOrderId());
//            if (row == null) throw new BusinessException(PaymentErrorCode.ORDER_NOT_FOUND);
//            expectedAmount = (BigDecimal) row.get("finalAmount");
//        } else {
//            throw new BusinessException(PaymentErrorCode.UNSUPPORTED_PAYMENT_TYPE);
//        }
//
//        // 금액 검증
//        if (payments.getAmount().compareTo(expectedAmount) != 0)
//            throw new BusinessException(PaymentErrorCode.AMOUNT_MISMATCH);


        payments.approve();
        paymentRepository.updateStatus(payments);
//        if(true) throw new RuntimeException("강제 예외");
        /*  TODO: 멤버십 생성 or 갱신 로직 (Memberships 테이블 생기면 구현
        if (payments.getPaymentType() == PaymentType.MEMBERSHIP) {
         */
        return PaymentResponseDto.from(payments);
    }

    @Transactional
    public PaymentResponseDto failedPaymentById(Long paymentId) {
        Payments payments = paymentRepository.findById(paymentId);
        if (payments.getStatus() != PaymentStatus.PENDING) {
            throw new BusinessException(PaymentErrorCode.INVALID_STATUS);
        }
        payments.failed();
        paymentRepository.updateStatus(payments);
        paymentRollbackService.rollbackStock(payments);
        return PaymentResponseDto.from(payments);
    }

    @Transactional
    public PaymentResponseDto cancelledPaymentById(Long paymentId) {
        Payments payments = paymentRepository.findById(paymentId);
        payments.cancel();
        paymentRepository.updateStatus(payments);
        return PaymentResponseDto.from(payments);
    }

    private BigDecimal getExpectedAmountMock(Payments payments){
        if (payments.getOrderId() != null) {
            return new BigDecimal("38000"); // 주문 총 금액 mock
        }
        if (payments.getReservationId() != null) {
            return new BigDecimal("12000"); // 예매 금액 mock
        }
        if (payments.getMembershipId() != null) {
            return new BigDecimal("10000"); // 멤버십 월 구독료 mock
        }
        throw new BusinessException(PaymentErrorCode.UNSUPPORTED_PAYMENT_TYPE);
    }
}