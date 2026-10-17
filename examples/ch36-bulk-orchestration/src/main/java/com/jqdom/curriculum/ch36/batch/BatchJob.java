package com.jqdom.curriculum.ch36.batch;

/** 배치 작업 상태. status는 RUNNING, DONE, DONE_WITH_FAILURES, FAILED 중 하나. */
public record BatchJob(long id, String status, int processed, int failed, String message) {
}
