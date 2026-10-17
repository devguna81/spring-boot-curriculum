package com.devguna.curriculum.ch07;

/** 응답 본문. 내부 객체를 그대로 노출하지 않고 응답용 형태로 한 번 감싼다. */
public record UserResponse(long id, String name, String email, int age) {

    public static UserResponse from(User user) {
        return new UserResponse(user.id(), user.name(), user.email(), user.age());
    }
}
