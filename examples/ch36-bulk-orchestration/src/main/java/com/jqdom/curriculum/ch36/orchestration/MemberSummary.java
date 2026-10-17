package com.jqdom.curriculum.ch36.orchestration;

/** 회원 한 명의 요약. 모듈 호출이 실패해 얻지 못한 값은 null이다. */
public record MemberSummary(long id, String name, String grade, Long points, Long orderCount) {
}
