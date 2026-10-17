package com.devguna.curriculum.ch24.application.port;

/** 외부 호출 실패를 HTTP 클라이언트 예외 대신 응용 계층의 말로 전달한다. */
public class AddressLookupUnavailableException extends RuntimeException {

    public AddressLookupUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }
}
