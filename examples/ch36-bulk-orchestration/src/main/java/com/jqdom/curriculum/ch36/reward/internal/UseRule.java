package com.jqdom.curriculum.ch36.reward.internal;

import java.util.List;

/** 사용(음수) 합계. 결과는 0 이하다. */
class UseRule implements PointRule {

    @Override
    public long points(List<LedgerRow> rows) {
        return rows.stream().mapToLong(LedgerRow::amount).filter(a -> a < 0).sum();
    }
}
