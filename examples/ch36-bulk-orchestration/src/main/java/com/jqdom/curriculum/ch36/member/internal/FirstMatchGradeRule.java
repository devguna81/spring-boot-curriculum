package com.jqdom.curriculum.ch36.member.internal;

import java.util.List;
import java.util.Optional;

/** 규칙 묶음: 앞에서부터 처음 해당하는 규칙의 결과를 쓴다. 맞는 규칙이 없으면 기본 등급. */
class FirstMatchGradeRule implements GradeRule {

    private final List<GradeRule> rules;
    private final String defaultGrade;

    FirstMatchGradeRule(List<GradeRule> rules, String defaultGrade) {
        this.rules = List.copyOf(rules);
        this.defaultGrade = defaultGrade;
    }

    @Override
    public Optional<String> grade(MemberRow member) {
        for (GradeRule rule : rules) {
            Optional<String> grade = rule.grade(member);
            if (grade.isPresent()) {
                return grade;
            }
        }
        return Optional.of(defaultGrade);
    }
}
