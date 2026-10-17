package com.jqdom.curriculum.ch07;

public class UserNotFoundException extends BusinessException {

    public UserNotFoundException(long id) {
        super(ErrorCode.NOT_FOUND, "사용자를 찾을 수 없습니다. id=" + id);
    }
}
