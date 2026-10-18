package com.jqdom.curriculum.ch36.order.api;

import java.util.Collection;
import java.util.Map;

/**
 * order 모듈의 입구.
 * orderCountsOf: 값 목록을 한 번에 받아 회원별 주문 건수를 돌려준다.
 * 빈 목록은 빈 결과, 중복은 한 번만 조회한다. 주문이 없는 회원은 키를 두지 않는다.
 */
public interface OrderApi {

    Map<Long, Long> orderCountsOf(Collection<Long> memberIds);
}
