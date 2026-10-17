package com.jqdom.curriculum.ch26.member.api;

/** member 모듈이 다른 모듈에 공개하는 유일한 입구. */
public interface MemberApi {

    boolean exists(long memberId);
}
