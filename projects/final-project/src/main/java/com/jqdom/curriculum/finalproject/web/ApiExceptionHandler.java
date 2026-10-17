package com.jqdom.curriculum.finalproject.web;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import com.jqdom.curriculum.finalproject.common.ForbiddenOperationException;
import com.jqdom.curriculum.finalproject.common.InvalidRequestException;
import com.jqdom.curriculum.finalproject.common.UnauthorizedException;
import com.jqdom.curriculum.finalproject.contract.ErrorBody;
import com.jqdom.curriculum.finalproject.contract.FieldIssue;
import com.jqdom.curriculum.finalproject.post.api.PostNotFoundException;
import com.jqdom.curriculum.finalproject.user.api.DuplicateUsernameException;

/** 모든 오류를 같은 모양(ErrorBody)으로 돌려준다. 내부 예외 메시지나 스택은 응답에 싣지 않는다. */
@RestControllerAdvice
class ApiExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<ErrorBody> validation(MethodArgumentNotValidException e) {
        List<FieldIssue> issues = e.getBindingResult().getFieldErrors().stream()
                .map(error -> new FieldIssue(error.getField(), error.getDefaultMessage()))
                .toList();
        return respond(HttpStatus.BAD_REQUEST, new ErrorBody("VALIDATION_FAILED", "입력값이 올바르지 않습니다", issues));
    }

    @ExceptionHandler(InvalidRequestException.class)
    ResponseEntity<ErrorBody> invalid(InvalidRequestException e) {
        return respond(HttpStatus.BAD_REQUEST, new ErrorBody("VALIDATION_FAILED", "입력값이 올바르지 않습니다", e.issues()));
    }

    @ExceptionHandler({HttpMessageNotReadableException.class, MethodArgumentTypeMismatchException.class})
    ResponseEntity<ErrorBody> unreadable(Exception e) {
        return respond(HttpStatus.BAD_REQUEST, ErrorBody.of("VALIDATION_FAILED", "요청 형식이 올바르지 않습니다"));
    }

    @ExceptionHandler(UnauthorizedException.class)
    ResponseEntity<ErrorBody> unauthorized(UnauthorizedException e) {
        return respond(HttpStatus.UNAUTHORIZED, ErrorBody.of("UNAUTHORIZED", e.getMessage()));
    }

    @ExceptionHandler(ForbiddenOperationException.class)
    ResponseEntity<ErrorBody> forbidden(ForbiddenOperationException e) {
        return respond(HttpStatus.FORBIDDEN, ErrorBody.of("FORBIDDEN", e.getMessage()));
    }

    @ExceptionHandler(PostNotFoundException.class)
    ResponseEntity<ErrorBody> notFound(PostNotFoundException e) {
        return respond(HttpStatus.NOT_FOUND, ErrorBody.of("NOT_FOUND", e.getMessage()));
    }

    @ExceptionHandler(DuplicateUsernameException.class)
    ResponseEntity<ErrorBody> duplicate(DuplicateUsernameException e) {
        return respond(HttpStatus.CONFLICT, ErrorBody.of("DUPLICATE_USERNAME", e.getMessage()));
    }

    private static ResponseEntity<ErrorBody> respond(HttpStatus status, ErrorBody body) {
        return ResponseEntity.status(status).body(body);
    }
}
