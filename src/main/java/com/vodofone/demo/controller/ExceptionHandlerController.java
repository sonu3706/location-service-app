package com.vodofone.demo.controller;

import com.vodofone.demo.exception.CustomException;
import com.vodofone.demo.exception.ErrorResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;

@RestControllerAdvice
public class ExceptionHandlerController extends ResponseEntityExceptionHandler {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<?> handleCustomException(CustomException exception) {
        ErrorResponse errorResponse = ErrorResponse.builder().errorCode(exception.getCode().getName())
                .dateTime(LocalDateTime.now()).build();
        if (exception.getHttpStatus().is4xxClientError()) {
            errorResponse.setErrorMessage(exception.getMessage());
        } else if (exception.getHttpStatus().is5xxServerError()) {
            errorResponse.setErrorMessage("ERROR: A technical exception occurred");
        }
        return ResponseEntity.status(exception.getHttpStatus()).body(errorResponse);
    }
}
