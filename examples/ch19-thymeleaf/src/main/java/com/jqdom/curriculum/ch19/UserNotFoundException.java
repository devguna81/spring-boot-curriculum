package com.jqdom.curriculum.ch19;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/** 없는 사용자를 요청하면 404 상태가 되고, 브라우저 요청에는 error/404 템플릿이 사용된다. */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(long id) {
        super("사용자를 찾을 수 없습니다. id=" + id);
    }
}
