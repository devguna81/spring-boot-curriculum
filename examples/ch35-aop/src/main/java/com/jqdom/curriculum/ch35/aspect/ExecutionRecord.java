package com.jqdom.curriculum.ch35.aspect;

/** 메서드 한 번의 실행 기록. 메서드 이름은 "클래스.메서드" 형태다. */
public record ExecutionRecord(String method, long elapsedMillis, boolean success) {
}
