package com.jqdom.curriculum.ch36;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.jqdom.curriculum.ch36.batch.BatchAlreadyRunningException;
import com.jqdom.curriculum.ch36.batch.BatchJob;
import com.jqdom.curriculum.ch36.batch.SummaryBatchRunner;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.test.web.servlet.MockMvc;

/** 온디맨드 배치. chunk 크기를 2로 줄여 [1,2] [3,4] [5] 세 번에 나누어 처리하게 한다. */
@SpringBootTest(properties = {
        "bulk.batch-chunk-size=2",
        "bulk.batch-async=false"})
@AutoConfigureMockMvc
class BatchTests {

    @Autowired
    SummaryBatchRunner runner;

    @Autowired
    JdbcClient jdbc;

    @Autowired
    MockMvc mvc;

    @BeforeEach
    void clean() {
        jdbc.sql("DELETE FROM member_summary_snapshot").update();
        jdbc.sql("DELETE FROM batch_job").update();
    }

    private long snapshotCount() {
        return jdbc.sql("SELECT COUNT(*) FROM member_summary_snapshot").query(Long.class).single();
    }

    @Test
    void processesEveryMemberInChunks() {
        long jobId = runner.start();

        BatchJob job = runner.status(jobId);
        assertEquals("DONE", job.status());
        assertEquals(5, job.processed());
        assertEquals(0, job.failed());
        assertEquals(5, snapshotCount());

        var row = jdbc.sql("SELECT grade, points, order_count FROM member_summary_snapshot WHERE member_id = 1")
                .query().singleRow();
        assertEquals("SENIOR", row.get("GRADE"));
        assertEquals(600L, ((Number) row.get("POINTS")).longValue());
        assertEquals(3L, ((Number) row.get("ORDER_COUNT")).longValue());
    }

    @Test
    void runningAgainOverwritesInsteadOfDuplicating() {
        runner.start();
        long second = runner.start();

        assertEquals(5, snapshotCount());
        long fromSecondJob = jdbc.sql("SELECT COUNT(*) FROM member_summary_snapshot WHERE job_id = :j")
                .param("j", second).query(Long.class).single();
        assertEquals(5, fromSecondJob);
    }

    @Test
    void secondStartWhileRunningIsRejected() {
        jdbc.sql("INSERT INTO batch_job (status, processed, failed) VALUES ('RUNNING', 0, 0)").update();

        assertThrows(BatchAlreadyRunningException.class, () -> runner.start());
        assertEquals(0, snapshotCount());
    }

    @Test
    void apiStartsAndReportsTheJob() throws Exception {
        String body = mvc.perform(post("/api/batch/member-summaries"))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.jobId").exists())
                .andReturn().getResponse().getContentAsString();
        long jobId = Long.parseLong(body.replaceAll("\\D", ""));

        mvc.perform(get("/api/batch/member-summaries/" + jobId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("DONE"))
                .andExpect(jsonPath("$.processed").value(5));
    }

    @Test
    void unknownJobIs404() throws Exception {
        mvc.perform(get("/api/batch/member-summaries/999"))
                .andExpect(status().isNotFound());
    }
}
