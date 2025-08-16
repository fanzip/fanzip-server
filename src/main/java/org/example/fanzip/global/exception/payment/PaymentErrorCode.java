package org.example.fanzip.global.exception.payment;

import org.example.fanzip.global.exception.ErrorCode;
import org.springframework.http.HttpStatus;

public enum PaymentErrorCode implements ErrorCode {
    PAYMENT_NOT_FOUND("PAYMENT_001", HttpStatus.NOT_FOUND, "해당 결제 정보를 찾을 수 없습니다."),
    DUPLICATE_TRANSACTION("PAYMENT_002", HttpStatus.BAD_REQUEST, "이미 처리된 결제입니다."),
    INVALID_TARGET_COUNT("PAYMENT_003", HttpStatus.BAD_REQUEST, "orderId, reservationId, membershipId 중 정확히 하나만 존재해야 합니다."),
    UNSUPPORTED_PAYMENT_TYPE("PAYMENT_004", HttpStatus.BAD_REQUEST, "지원하지 않는 결제 유형입니다."),
    AMOUNT_MISMATCH("PAYMENT_005", HttpStatus.BAD_REQUEST, "결제 요청 금액이 실제 금액과 일치하지 않습니다."),
    INVALID_STATUS("PAYMENT_006", HttpStatus.CONFLICT, "현재 상태에서는 결제를 실패 처리할 수 없습니다."),
    ALREADY_SUBSCRIBED("PAYMENT_007", HttpStatus.BAD_REQUEST, "이미 구독 중인 멤버십입니다."),
    ORDER_STOCK_UNAVAILABLE("STOCK_001", HttpStatus.CONFLICT, "상품 재고가 부족합니다."),
    SEATS_UNAVAILABLE("STOCK_002", HttpStatus.CONFLICT, "예약 가능한 인원이 없습니다."),
    MEMBERSHIP_NOT_FOUND("MEMBERSHIP_001", HttpStatus.NOT_FOUND, "멤버십 가입 정보를 찾을 수 없습니다."),
    MEMBERSHIP_ACTIVATION_FAILED("MEMBERSHIP_002", HttpStatus.INTERNAL_SERVER_ERROR, "멤버십 활성화에 실패했습니다."),
    FANCARD_CREATION_FAILED("FANCARD_001", HttpStatus.INTERNAL_SERVER_ERROR, "팬카드 생성에 실패했습니다."),
    ORDER_NOT_FOUND("ORDER_001", HttpStatus.NOT_FOUND, "주문을 찾을 수 없습니다.");

    private final String code;
    private final HttpStatus status;
    private final String message;

    PaymentErrorCode(String code, HttpStatus status, String message) {
        this.code = code;
        this.status = status;
        this.message = message;
    }

    @Override
    public HttpStatus getStatus() {
        return status;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}