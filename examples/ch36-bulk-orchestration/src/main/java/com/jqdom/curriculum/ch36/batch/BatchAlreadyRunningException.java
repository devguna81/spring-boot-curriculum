package com.jqdom.curriculum.ch36.batch;

public class BatchAlreadyRunningException extends RuntimeException {

    public BatchAlreadyRunningException(long runningJobId) {
        super("이미 실행 중인 배치가 있습니다: jobId=" + runningJobId);
    }
}
