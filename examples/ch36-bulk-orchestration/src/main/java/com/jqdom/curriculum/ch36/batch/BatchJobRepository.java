package com.jqdom.curriculum.ch36.batch;

import java.util.Optional;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;

/** 배치 작업 상태와 결과 스냅샷을 저장한다. 이 표들은 batch 쪽이 소유한다. */
@Repository
class BatchJobRepository {

    static final String RUNNING = "RUNNING";

    private final JdbcClient jdbc;

    BatchJobRepository(JdbcClient jdbc) {
        this.jdbc = jdbc;
    }

    Optional<Long> findRunningJobId() {
        return jdbc.sql("SELECT id FROM batch_job WHERE status = :s ORDER BY id FETCH FIRST 1 ROWS ONLY")
                .param("s", RUNNING).query(Long.class).optional();
    }

    long create() {
        GeneratedKeyHolder keys = new GeneratedKeyHolder();
        jdbc.sql("INSERT INTO batch_job (status, processed, failed) VALUES (:s, 0, 0)")
                .param("s", RUNNING).update(keys, "id");
        return keys.getKey().longValue();
    }

    void updateProgress(long jobId, int processed, int failed) {
        jdbc.sql("UPDATE batch_job SET processed = :p, failed = :f WHERE id = :id")
                .param("p", processed).param("f", failed).param("id", jobId).update();
    }

    void finish(long jobId, String status, String message) {
        jdbc.sql("UPDATE batch_job SET status = :s, message = :m WHERE id = :id")
                .param("s", status).param("m", message).param("id", jobId).update();
    }

    Optional<BatchJob> find(long jobId) {
        return jdbc.sql("SELECT id, status, processed, failed, message FROM batch_job WHERE id = :id")
                .param("id", jobId).query(BatchJob.class).optional();
    }

    /** 같은 회원은 덮어쓴다. 다시 실행해도 결과가 늘어나지 않는다(멱등). H2의 MERGE ... KEY 문법이다. */
    void upsertSnapshot(long memberId, String grade, long points, long orderCount, long jobId) {
        jdbc.sql("MERGE INTO member_summary_snapshot (member_id, grade, points, order_count, job_id) "
                        + "KEY (member_id) VALUES (:m, :g, :p, :c, :j)")
                .param("m", memberId).param("g", grade).param("p", points)
                .param("c", orderCount).param("j", jobId).update();
    }
}
