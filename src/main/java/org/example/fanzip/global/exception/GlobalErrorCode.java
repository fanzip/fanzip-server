package org.example.fanzip.global.exception;


import org.springframework.http.HttpStatus;

public enum GlobalErrorCode implements ErrorCode {
    INVALID_INPUT_VALUE("G001", HttpStatus.BAD_REQUEST, "잘못된 입력입니다."),
    ENTITY_NOT_FOUND("G002", HttpStatus.NOT_FOUND, "데이터를 찾을 수 없습니다."),
    INTERNAL_SERVER_ERROR("G999", HttpStatus.INTERNAL_SERVER_ERROR, "서버 오류가 발생했습니다.");

    private final String code;
    private final HttpStatus status;
    private final String message;

    GlobalErrorCode(String code, HttpStatus status, String message) {
        this.code = code;
        this.status = status;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }
}