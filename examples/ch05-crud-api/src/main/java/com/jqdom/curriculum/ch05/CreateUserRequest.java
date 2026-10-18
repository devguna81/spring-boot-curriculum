package com.jqdom.curriculum.ch05;

/** POST /users 요청 본문. id는 서버가 정하므로 받지 않는다. */
public record CreateUserRequest(String name, String email, int age) {
}
