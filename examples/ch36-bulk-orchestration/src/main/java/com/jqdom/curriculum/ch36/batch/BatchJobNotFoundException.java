package com.jqdom.curriculum.ch36.batch;

public class BatchJobNotFoundException extends RuntimeException {

    public BatchJobNotFoundException(long jobId) {
        super("배치 작업을 찾을 수 없습니다: jobId=" + jobId);
    }
}
