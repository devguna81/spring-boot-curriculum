package com.devguna.curriculum.ch17;

/** 저장소가 다루는 내부 객체. 외부와 주고받는 형태(DTO)와 분리해 둔다. */
public record User(long id, String name, String email, int age) {
}
