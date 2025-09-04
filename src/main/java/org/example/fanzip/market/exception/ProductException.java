package org.example.fanzip.market.exception;

import org.example.fanzip.global.exception.BusinessException;

public class ProductException extends BusinessException {
    
    public ProductException(MarketErrorCode errorCode) {
        super(errorCode);
    }
}