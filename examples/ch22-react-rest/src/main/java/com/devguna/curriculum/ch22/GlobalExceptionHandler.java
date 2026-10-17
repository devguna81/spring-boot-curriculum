package com.devguna.curriculum.ch22;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorBody> validation(MethodArgumentNotValidException e) {
        List<ErrorBody.FieldIssue> issues = e.getBindingResult().getFieldErrors().stream()
                .map(error -> new ErrorBody.FieldIssue(error.getField(), error.getDefaultMessage()))
                .toList();
        return ResponseEntity.badRequest()
                .body(new ErrorBody("VALIDATION_FAILED", "요청 값이 올바르지 않습니다", issues));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorBody> unreadable(HttpMessageNotReadableException e) {
        return ResponseEntity.badRequest()
                .body(new ErrorBody("MALFORMED_REQUEST", "요청 본문을 읽을 수 없습니다", List.of()));
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorBody> notFound(UserNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorBody("USER_NOT_FOUND", e.getMessage(), List.of()));
    }

    @ExceptionHandler(DuplicateEmailException.class)
    public ResponseEntity<ErrorBody> duplicate(DuplicateEmailException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorBody("DUPLICATE_EMAIL", e.getMessage(),
                List.of(new ErrorBody.FieldIssue("email", e.getMessage()))));
    }
}
