package com.jqdom.curriculum.ch36.reward.internal;

import java.util.List;

/** 원장 행 목록에서 포인트 한 갈래를 계산하는 규칙. 단일 규칙과 합산 묶음이 같은 모양이다. */
interface PointRule {

    long points(List<LedgerRow> rows);
}
