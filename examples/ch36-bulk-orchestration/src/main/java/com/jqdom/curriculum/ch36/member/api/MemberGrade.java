package com.jqdom.curriculum.ch36.member.api;

/** member 모듈이 공개하는 회원 요약. 내부 행(MemberRow)은 밖으로 나가지 않는다. */
public record MemberGrade(long id, String name, String grade) {
}
