package com.jqdom.curriculum.ch36.member.internal;

import java.util.Optional;

/** 가입 연도가 기준 연도 이하이면 SENIOR. */
class SeniorRule implements GradeRule {

    private final int thresholdYear;

    SeniorRule(int thresholdYear) {
        this.thresholdYear = thresholdYear;
    }

    @Override
    public Optional<String> grade(MemberRow member) {
        return member.joinedYear() <= thresholdYear ? Optional.of("SENIOR") : Optional.empty();
    }
}
