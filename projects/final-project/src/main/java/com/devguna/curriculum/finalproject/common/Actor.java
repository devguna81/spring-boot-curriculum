package com.devguna.curriculum.finalproject.common;

import com.devguna.curriculum.finalproject.contract.Role;

/** 요청을 보낸 사용자. 서비스는 HTTP나 보안 설정을 모르고 이 값만 받는다. */
public record Actor(long id, Role role) {

    public boolean isAdmin() {
        return role == Role.ADMIN;
    }
}
