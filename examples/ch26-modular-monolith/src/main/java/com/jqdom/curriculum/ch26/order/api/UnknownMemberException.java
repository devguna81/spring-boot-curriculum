package com.jqdom.curriculum.ch26.order.api;

public class UnknownMemberException extends RuntimeException {

    public UnknownMemberException(long memberId) {
        super("존재하지 않는 회원입니다: " + memberId);
    }
}
