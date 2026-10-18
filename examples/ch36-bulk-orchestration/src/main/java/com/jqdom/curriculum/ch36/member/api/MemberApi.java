package com.jqdom.curriculum.ch36.member.api;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * member 모듈이 다른 모듈에 공개하는 유일한 입구.
 *
 * <p>계약:
 * <ul>
 *   <li>gradesOf: 값 목록을 한 번에 받는다. 빈 목록이면 빈 결과, 중복은 한 번만 조회한다.
 *       없는 회원은 결과에 키를 두지 않는다(예외가 아니다). 목록이 null이면 IllegalArgumentException.
 *       목록 길이에는 제한이 없다. IN 길이는 모듈 안에서 나누어 처리한다.</li>
 *   <li>memberIdsAfter: 배치가 전체를 한 번에 읽지 않도록 id 순서로 이어 읽는다.</li>
 * </ul>
 */
public interface MemberApi {

    Map<Long, MemberGrade> gradesOf(Collection<Long> memberIds);

    List<Long> memberIdsAfter(long lastId, int limit);
}
