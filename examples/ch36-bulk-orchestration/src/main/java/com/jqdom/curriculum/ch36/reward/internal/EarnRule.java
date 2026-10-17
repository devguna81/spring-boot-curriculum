package com.jqdom.curriculum.ch36.reward.internal;

import java.util.List;

/** 적립(양수) 합계. */
class EarnRule implements PointRule {

    @Override
    public long points(List<LedgerRow> rows) {
        return rows.stream().mapToLong(LedgerRow::amount).filter(a -> a > 0).sum();
    }
}
