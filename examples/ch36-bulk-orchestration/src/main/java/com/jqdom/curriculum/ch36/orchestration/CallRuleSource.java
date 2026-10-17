package com.jqdom.curriculum.ch36.orchestration;

import java.util.List;

/**
 * 호출 규칙을 가져오는 곳. 이 예제는 코드로 정의한 규칙을 쓴다(CodeCallRules).
 * 규칙을 DB 표로 옮기려면 이 인터페이스의 구현만 바꾸면 되고 Orchestrator는 바뀌지 않는다.
 */
public interface CallRuleSource {

    List<CallRule> rules();
}
