package com.devguna.curriculum.ch05;

/** PUT /users/{id} 요청 본문. PUT은 리소스 전체를 교체하므로 모든 필드를 받는다. */
public record UpdateUserRequest(String name, String email, int age) {
}
