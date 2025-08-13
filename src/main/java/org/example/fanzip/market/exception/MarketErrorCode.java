package org.example.fanzip.market.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.example.fanzip.global.exception.ErrorCode;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum MarketErrorCode implements ErrorCode {
    // 상품 관련
    PRODUCT_NOT_FOUND("M001", HttpStatus.NOT_FOUND, "상품을 찾을 수 없습니다"),
    INVALID_PRODUCT_NAME("M002", HttpStatus.BAD_REQUEST, "상품명은 필수이며 255자 이하여야 합니다"),
    INVALID_PRODUCT_PRICE("M003", HttpStatus.BAD_REQUEST, "상품 가격은 0보다 커야 합니다"),
    INVALID_DISCOUNT_PRICE("M004", HttpStatus.BAD_REQUEST, "할인 가격이 원가보다 클 수 없습니다"),
    INVALID_STOCK_QUANTITY("M005", HttpStatus.BAD_REQUEST, "재고는 0 이상이어야 합니다"),
    INVALID_SHIPPING_PRICE("M006", HttpStatus.BAD_REQUEST, "배송비는 0 이상이어야 합니다"),
    PRODUCT_SAVE_FAILED("M007", HttpStatus.INTERNAL_SERVER_ERROR, "상품 저장에 실패했습니다"),
    
    // 인플루언서 관련
    INFLUENCER_NOT_FOUND("M101", HttpStatus.NOT_FOUND, "인플루언서를 찾을 수 없습니다"),
    UNAUTHORIZED_INFLUENCER("M102", HttpStatus.FORBIDDEN, "해당 인플루언서의 상품을 등록할 권한이 없습니다"),
    
    // 이미지 관련
    INVALID_IMAGE_URL("M201", HttpStatus.BAD_REQUEST, "올바르지 않은 이미지 URL입니다"),
    IMAGE_UPLOAD_FAILED("M202", HttpStatus.INTERNAL_SERVER_ERROR, "이미지 업로드에 실패했습니다"),
    
    // 판매 시간 관련
    INVALID_SALE_TIME("M301", HttpStatus.BAD_REQUEST, "판매 시작 시간이 올바르지 않습니다"),
    PAST_SALE_TIME("M302", HttpStatus.BAD_REQUEST, "과거 시간으로 판매 시작 시간을 설정할 수 없습니다");

    private final String code;
    private final HttpStatus status;
    private final String message;
}