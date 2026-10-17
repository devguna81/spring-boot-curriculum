package com.devguna.curriculum.ch24.domain;

/** 우리 서비스가 주소를 다루는 모양. 외부 API의 필드 이름과는 무관하다. */
public record Address(String postalCode, String city, String district, String street) {
}
