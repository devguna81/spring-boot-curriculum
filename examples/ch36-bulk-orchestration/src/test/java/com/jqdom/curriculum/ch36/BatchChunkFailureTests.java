package com.jqdom.curriculum.ch36;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.jqdom.curriculum.ch36.batch.BatchJob;
import com.jqdom.curriculum.ch36.batch.SummaryBatchRunner;
import com.jqdom.curriculum.ch36.support.FailingRewardForMemberThree;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.simple.JdbcClient;

/** [3,4] 묶음만 실패하면 그 묶음의 2명은 실패로 세고, 앞뒤 묶음의 결과는 남는다. */
@SpringBootTest(properties = {
        "bulk.batch-chunk-size=2",
        "bulk.batch-async=false"})
@Import(FailingRewardForMemberThree.class)
class BatchChunkFailureTests {

    @Autowired
    SummaryBatchRunner runner;

    @Autowired
    JdbcClient jdbc;

    @Test
    void failedChunkDoesNotRemoveTheOthers() {
        long jobId = runner.start();

        BatchJob job = runner.status(jobId);
        assertEquals("DONE_WITH_FAILURES", job.status());
        assertEquals(3, job.processed());
        assertEquals(2, job.failed());
        assertEquals("필수 호출이 실패했습니다: reward", job.message());

        var ids = jdbc.sql("SELECT member_id FROM member_summary_snapshot ORDER BY member_id")
                .query(Long.class).list();
        assertEquals(java.util.List.of(1L, 2L, 5L), ids);
    }
}
