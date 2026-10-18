package com.jqdom.curriculum.ch36.member.internal;

import java.util.Optional;

/** 회원 등급 규칙 하나. 해당하지 않으면 빈 값. 단일 규칙과 묶음이 같은 모양이다(ch25 Composite와 같은 방식). */
interface GradeRule {

    Optional<String> grade(MemberRow member);
}
