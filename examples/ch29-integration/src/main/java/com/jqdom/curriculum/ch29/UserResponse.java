package com.jqdom.curriculum.ch29;

import java.time.LocalDateTime;

public record UserResponse(long id, String name, String email, int age, LocalDateTime createdAt) {

    public static UserResponse from(User user) {
        return new UserResponse(user.getId(), user.getName(), user.getEmail(), user.getAge(), user.getCreatedAt());
    }
}
