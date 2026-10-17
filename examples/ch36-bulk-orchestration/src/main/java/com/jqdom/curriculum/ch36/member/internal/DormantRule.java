package com.jqdom.curriculum.ch36.member.internal;

import java.util.Optional;

class DormantRule implements GradeRule {

    @Override
    public Optional<String> grade(MemberRow member) {
        return member.dormant() ? Optional.of("DORMANT") : Optional.empty();
    }
}
