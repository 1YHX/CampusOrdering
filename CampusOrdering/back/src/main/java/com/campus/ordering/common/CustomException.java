package com.campus.ordering.common;

/**
 * 自定义业务异常
 */
public class CustomException extends RuntimeException {
    
    private static final long serialVersionUID = 1L;
    
    public CustomException(String message) {
        super(message);
    }
} 