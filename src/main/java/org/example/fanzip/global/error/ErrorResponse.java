package org.example.fanzip.global.error;

import org.example.fanzip.global.exception.ErrorCode;

public class ErrorResponse {
    private final int status;
    private final String message;

    public ErrorResponse(ErrorCode errorCode) {
        this.status = errorCode.getStatus().value();
        this.message = errorCode.getMessage();
    }

    public ErrorResponse(ErrorCode errorCode, String customMessage) {
        this.status = errorCode.getStatus().value();
        this.message = customMessage;
    }

    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }
}