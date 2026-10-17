package com.jqdom.curriculum.ch36.reward.internal;

import java.util.List;

public final class RewardRuleTestAccess {

    private RewardRuleTestAccess() {
    }

    public static long points(List<Long> amounts) {
        PointRule rule = new SumPointRule(List.of(new EarnRule(), new UseRule()));
        return rule.points(amounts.stream().map(a -> new LedgerRow(1, a)).toList());
    }
}
