package com.devguna.curriculum.ch04;

/** 응답으로 내려가는 사용자. record의 컴포넌트 이름이 그대로 JSON 키가 된다. */
public record User(long id, String name, String email, int age) {
}
