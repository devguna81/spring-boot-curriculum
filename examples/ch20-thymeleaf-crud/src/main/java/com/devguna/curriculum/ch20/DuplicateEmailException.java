package com.devguna.curriculum.ch20;

/** 이미 있는 이메일로 등록/수정하려 할 때. 컨트롤러가 필드 오류(email)로 바꿔 화면에 보여 준다. */
public class DuplicateEmailException extends RuntimeException {

    public DuplicateEmailException(String email) {
        super("이미 사용 중인 이메일입니다: " + email);
    }
}
