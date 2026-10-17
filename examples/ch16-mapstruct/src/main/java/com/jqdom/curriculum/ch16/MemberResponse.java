package com.jqdom.curriculum.ch16;

/** 응답. 비밀번호 해시와 내부 필드는 아예 선언하지 않는다. 선언하지 않은 필드는 나갈 수 없다. */
public record MemberResponse(long id, String name, String maskedEmail, int age, String statusLabel, String joinedDate) {
}
