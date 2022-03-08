package com.vodofone.demo.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

public class CustomException extends Exception {
    private static final long serialVersionUID = 1L;
    private String timestamp;
    @Getter
    private ErrorCode code;
    @Getter
    private HttpStatus httpStatus;

    public CustomException(ErrorCode code, String message) {
        super(message);
        timestamp = String.valueOf(System.nanoTime());
        this.code = code;
    }

    public CustomException(ErrorCode code, String message, HttpStatus httpStatus) {
        this(code, message);
        timestamp = String.valueOf(System.nanoTime());
        this.httpStatus = httpStatus;
    }
}
