package com.devguna.curriculum.ch24.application.usecase;

public class MemberNotFoundException extends RuntimeException {

    public MemberNotFoundException(long id) {
        super("회원을 찾을 수 없습니다: " + id);
    }
}
