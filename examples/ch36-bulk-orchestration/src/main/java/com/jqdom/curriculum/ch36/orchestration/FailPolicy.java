package com.jqdom.curriculum.ch36.orchestration;

/**
 * Handler 호출이 실패했을 때의 처리.
 * STOP: 전체를 중단하고 예외를 올린다. CONTINUE: 실패를 기록하고 다음으로 간다.
 * WARNING: CONTINUE와 같고 응답의 경고 목록에도 남긴다. SKIP: 실패가 아니라 건너뜀으로 기록한다.
 */
public enum FailPolicy {
    STOP, CONTINUE, WARNING, SKIP
}
