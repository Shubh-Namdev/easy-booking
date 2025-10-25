package com.example.booking.config;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, Object> body = new HashMap<>();
        var err = ex.getBindingResult().getFieldError();
        body.put("error", "Validation Failed");
        body.put("message", err != null ? err.getDefaultMessage() : "Invalid input");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }
}
