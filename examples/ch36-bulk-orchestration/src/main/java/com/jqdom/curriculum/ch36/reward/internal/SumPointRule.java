package com.jqdom.curriculum.ch36.reward.internal;

import java.util.List;

/** 규칙 묶음: 자식 규칙의 결과를 모두 더한다. */
class SumPointRule implements PointRule {

    private final List<PointRule> rules;

    SumPointRule(List<PointRule> rules) {
        this.rules = List.copyOf(rules);
    }

    @Override
    public long points(List<LedgerRow> rows) {
        return rules.stream().mapToLong(rule -> rule.points(rows)).sum();
    }
}
