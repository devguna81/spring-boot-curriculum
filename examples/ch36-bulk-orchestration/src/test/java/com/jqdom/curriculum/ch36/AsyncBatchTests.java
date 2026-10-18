package com.jqdom.curriculum.ch36;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.jqdom.curriculum.ch36.batch.BatchJob;
import com.jqdom.curriculum.ch36.batch.SummaryBatchRunner;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/** 기본 설정(비동기)에서는 start()가 작업 번호만 돌려주고, 상태가 DONE이 될 때까지 조회로 확인한다. */
@SpringBootTest(properties = "bulk.batch-chunk-size=2")
class AsyncBatchTests {

    @Autowired
    SummaryBatchRunner runner;

    @Test
    void jobFinishesInTheBackground() throws Exception {
        long jobId = runner.start();

        BatchJob job = runner.status(jobId);
        long deadline = System.currentTimeMillis() + 10_000;
        while ("RUNNING".equals(job.status()) && System.currentTimeMillis() < deadline) {
            Thread.sleep(50);
            job = runner.status(jobId);
        }

        assertEquals("DONE", job.status());
        assertEquals(5, job.processed());
        assertTrue(job.failed() == 0);
    }
}
