package com.jqdom.curriculum.ch18.web;

import java.util.List;

import com.jqdom.curriculum.ch18.contract.ErrorBody;
import com.jqdom.curriculum.ch18.contract.FieldIssue;
import com.jqdom.curriculum.ch18.internal.DuplicateEmailException;
import com.jqdom.curriculum.ch18.internal.UserNotFoundException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorBody> validation(MethodArgumentNotValidException e) {
        List<FieldIssue> issues = e.getBindingResult().getFieldErrors().stream()
                .map(error -> new FieldIssue(error.getField(), error.getDefaultMessage()))
                .toList();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorBody("VALIDATION_FAILED", "요청 값이 올바르지 않습니다", issues));
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorBody> notFound(UserNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorBody("USER_NOT_FOUND", e.getMessage(), List.of()));
    }

    @ExceptionHandler(DuplicateEmailException.class)
    public ResponseEntity<ErrorBody> duplicate(DuplicateEmailException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ErrorBody("DUPLICATE_EMAIL", e.getMessage(), List.of()));
    }
}
