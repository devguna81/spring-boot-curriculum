package com.jqdom.curriculum.ch23.service;

public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(long id) {
        super("사용자를 찾을 수 없습니다: " + id);
    }
}
