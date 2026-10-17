package com.jqdom.curriculum.ch36.orchestration;

/** Handler 하나의 실행 결과. status는 OK, FAILED, SKIPPED 중 하나. */
public record CallOutcome(String key, String status, String message) {

    static CallOutcome ok(String key) {
        return new CallOutcome(key, "OK", null);
    }

    static CallOutcome failed(String key, String message) {
        return new CallOutcome(key, "FAILED", message);
    }

    static CallOutcome skipped(String key, String message) {
        return new CallOutcome(key, "SKIPPED", message);
    }
}
