package com.jqdom.curriculum.ch24.application.usecase;

public class UnknownPostalCodeException extends RuntimeException {

    public UnknownPostalCodeException(String postalCode) {
        super("존재하지 않는 우편번호입니다: " + postalCode);
    }
}
