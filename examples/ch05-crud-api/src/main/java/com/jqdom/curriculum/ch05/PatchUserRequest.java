package com.jqdom.curriculum.ch05;

/**
 * PATCH /users/{id} 요청 본문. 바꾸고 싶은 필드만 보낸다.
 * 보내지 않은 필드는 null로 들어오므로 null이면 "변경 없음"으로 해석한다.
 * 그래서 age는 int가 아니라 Integer다. 이 방식은 값을 일부러 null로 지우는 요청과 구분할 수 없다는 한계가 있다.
 */
public record PatchUserRequest(String name, String email, Integer age) {
}
