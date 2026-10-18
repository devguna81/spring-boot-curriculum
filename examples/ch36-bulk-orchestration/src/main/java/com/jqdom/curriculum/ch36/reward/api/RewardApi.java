package com.jqdom.curriculum.ch36.reward.api;

import java.util.Collection;
import java.util.Map;

/**
 * reward 모듈의 입구.
 * pointsOf: 값 목록을 한 번에 받아 회원별 적립 포인트 합계를 돌려준다.
 * 빈 목록은 빈 결과, 중복은 한 번만 조회한다. 원장 기록이 없는 회원은 키를 두지 않는다.
 */
public interface RewardApi {

    Map<Long, Long> pointsOf(Collection<Long> memberIds);
}
