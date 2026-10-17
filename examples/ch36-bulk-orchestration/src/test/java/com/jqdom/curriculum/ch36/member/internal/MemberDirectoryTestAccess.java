package com.jqdom.curriculum.ch36.member.internal;

import java.util.List;

/** 패키지 전용 클래스를 테스트에서 쓰기 위한 도우미. 테스트 소스에만 있다. */
public final class MemberDirectoryTestAccess {

    private MemberDirectoryTestAccess() {
    }

    public static String grade(int joinedYear, boolean dormant) {
        GradeRule rule = new FirstMatchGradeRule(List.of(new DormantRule(), new SeniorRule(2020)), "REGULAR");
        return rule.grade(new MemberRow(1, "x", joinedYear, dormant)).orElseThrow();
    }
}
