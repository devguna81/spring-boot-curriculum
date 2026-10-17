package com.devguna.curriculum.ch24.adapter.web;

import com.devguna.curriculum.ch24.application.port.AddressLookupUnavailableException;
import com.devguna.curriculum.ch24.application.usecase.MemberNotFoundException;
import com.devguna.curriculum.ch24.application.usecase.UnknownPostalCodeException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UnknownPostalCodeException.class)
    ResponseEntity<Map<String, String>> unknownPostalCode(UnknownPostalCodeException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", e.getMessage()));
    }

    @ExceptionHandler(MemberNotFoundException.class)
    ResponseEntity<Map<String, String>> notFound(MemberNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", e.getMessage()));
    }

    /** 외부 의존성이 응답하지 못한 것은 우리 서버의 잘못이 아니라 일시적인 이용 불가다. */
    @ExceptionHandler(AddressLookupUnavailableException.class)
    ResponseEntity<Map<String, String>> unavailable(AddressLookupUnavailableException e) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(Map.of("message", "주소 확인 서비스를 잠시 사용할 수 없습니다"));
    }
}
