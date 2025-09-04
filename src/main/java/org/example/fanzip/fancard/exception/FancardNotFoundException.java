package org.example.fanzip.fancard.exception;

public class FancardNotFoundException extends RuntimeException {
    
    public FancardNotFoundException(String message) {
        super(message);
    }
    
    public FancardNotFoundException(Long cardId) {
        super("팬카드를 찾을 수 없습니다. (ID: " + cardId + ")");
    }
}