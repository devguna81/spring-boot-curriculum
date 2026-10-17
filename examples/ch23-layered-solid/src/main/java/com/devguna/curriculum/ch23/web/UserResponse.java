package com.devguna.curriculum.ch23.web;

import com.devguna.curriculum.ch23.domain.User;

public record UserResponse(long id, String name, String email) {

    static UserResponse from(User user) {
        return new UserResponse(user.id(), user.name(), user.email());
    }
}
