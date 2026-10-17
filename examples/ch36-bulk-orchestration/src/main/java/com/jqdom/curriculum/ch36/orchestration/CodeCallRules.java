package com.jqdom.curriculum.ch36.orchestration;

import java.util.List;

import org.springframework.stereotype.Component;

/** 코드로 정의한 호출 규칙. 회원 정보가 없으면 요약이 의미 없으므로 STOP, 나머지는 실패해도 계속한다. */
@Component
class CodeCallRules implements CallRuleSource {

    @Override
    public List<CallRule> rules() {
        return List.of(
                new CallRule("member", 1, FailPolicy.STOP),
                new CallRule("reward", 2, FailPolicy.CONTINUE),
                new CallRule("order", 3, FailPolicy.WARNING));
    }
}
