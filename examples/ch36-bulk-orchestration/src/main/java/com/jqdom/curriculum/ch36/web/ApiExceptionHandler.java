package com.jqdom.curriculum.ch36.web;

import java.util.List;
import java.util.Map;

import com.jqdom.curriculum.ch36.batch.BatchAlreadyRunningException;
import com.jqdom.curriculum.ch36.batch.BatchJobNotFoundException;
import com.jqdom.curriculum.ch36.orchestration.CallFailedException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
class ApiExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<Map<String, Object>> invalid(MethodArgumentNotValidException e) {
        List<String> messages = e.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getDefaultMessage()).toList();
        return ResponseEntity.badRequest().body(Map.of("message", "요청 값이 올바르지 않습니다.", "issues", messages));
    }

    @ExceptionHandler(CallFailedException.class)
    ResponseEntity<Map<String, String>> callFailed(CallFailedException e) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(Map.of("message", e.getMessage(), "handler", e.handlerKey()));
    }

    @ExceptionHandler(BatchAlreadyRunningException.class)
    ResponseEntity<Map<String, String>> running(BatchAlreadyRunningException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("message", e.getMessage()));
    }

    @ExceptionHandler(BatchJobNotFoundException.class)
    ResponseEntity<Map<String, String>> notFound(BatchJobNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", e.getMessage()));
    }
}
