package com.jqdom.curriculum.ch36.orchestration;

/** 호출 규칙 한 줄: 어떤 Handler를, 몇 번째로, 실패하면 어떻게 할지. */
public record CallRule(String handlerKey, int execSeq, FailPolicy failPolicy) {
}
