package org.example.fanzip.global.exception;

import org.example.fanzip.global.error.ErrorResponse;
import org.example.fanzip.market.exception.ProductException;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice // 모든 RestController 대상으로 동작
public class GlobalExceptionHandler {
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorResponse> handleCustomException(CustomException e) {
        ErrorCode errorCode = e.getErrorCode();
        ErrorResponse response = new ErrorResponse(errorCode);
        return new ResponseEntity<>(response, errorCode.getStatus());
    }

    // 상품 관련 예외 처리
    @ExceptionHandler(ProductException.class)
    public ResponseEntity<ErrorResponse> handleProductException(ProductException e) {
        ErrorCode errorCode = e.getErrorCode();
        ErrorResponse response = new ErrorResponse(errorCode);
        return new ResponseEntity<>(response, errorCode.getStatus());
    }

    // Bean Validation 예외 처리 (@Valid 어노테이션 검증 실패)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException e) {
        BindingResult bindingResult = e.getBindingResult();
        List<FieldError> fieldErrors = bindingResult.getFieldErrors();
        
        StringBuilder errorMessage = new StringBuilder();
        for (FieldError error : fieldErrors) {
            errorMessage.append(error.getField())
                       .append(": ")
                       .append(error.getDefaultMessage())
                       .append("; ");
        }
        
        ErrorResponse response = new ErrorResponse(GlobalErrorCode.INVALID_INPUT_VALUE, 
                errorMessage.toString());
        return new ResponseEntity<>(response, GlobalErrorCode.INVALID_INPUT_VALUE.getStatus());
    }

    // 기본 예외: IllegalArgumentException 처리
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException e) {
        ErrorResponse response = new ErrorResponse(GlobalErrorCode.INVALID_INPUT_VALUE, e.getMessage());
        return new ResponseEntity<>(response, GlobalErrorCode.INVALID_INPUT_VALUE.getStatus());
    }

    // 예상 못 한 모든 예외 처리 (500 에러)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e) {
        e.printStackTrace(); // 로컬 디버깅용
        ErrorResponse response = new ErrorResponse(GlobalErrorCode.INTERNAL_SERVER_ERROR, "예상치 못한 오류가 발생했습니다.");
        return new ResponseEntity<>(response, GlobalErrorCode.INTERNAL_SERVER_ERROR.getStatus());
    }
}