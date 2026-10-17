package com.jqdom.curriculum.ch36.batch;

import java.util.List;
import java.util.concurrent.Executor;

import com.jqdom.curriculum.ch36.member.api.MemberApi;
import com.jqdom.curriculum.ch36.orchestration.CallContext;
import com.jqdom.curriculum.ch36.orchestration.CallFailedException;
import com.jqdom.curriculum.ch36.orchestration.MemberSummary;
import com.jqdom.curriculum.ch36.orchestration.SummaryOrchestrator;
import com.jqdom.curriculum.ch36.orchestration.SummaryResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * 호출될 때 시작하는 온디맨드 배치. 회원 id를 번호 순서로 chunk 단위로 읽어,
 * 요청 경로와 같은 Orchestrator로 요약을 만들고 스냅샷 표에 덮어쓴다.
 *
 * <p>chunk마다 트랜잭션을 따로 쓰므로 한 chunk가 실패해도 앞 chunk의 결과는 남는다.
 * 같은 배치는 동시에 하나만 실행한다(이 예제는 서버 한 대 기준이다).
 */
@Service
public class SummaryBatchRunner {

    private static final Logger log = LoggerFactory.getLogger(SummaryBatchRunner.class);

    private final MemberApi members;
    private final SummaryOrchestrator orchestrator;
    private final BatchJobRepository jobs;
    private final TransactionTemplate tx;
    private final Executor executor;
    private final int chunkSize;

    SummaryBatchRunner(MemberApi members, SummaryOrchestrator orchestrator, BatchJobRepository jobs,
                       TransactionTemplate tx, @Qualifier("batchExecutor") Executor executor,
                       @Value("${bulk.batch-chunk-size}") int chunkSize) {
        this.members = members;
        this.orchestrator = orchestrator;
        this.jobs = jobs;
        this.tx = tx;
        this.executor = executor;
        this.chunkSize = chunkSize;
    }

    /** 작업을 만들고 실행을 맡긴 뒤 작업 번호를 바로 돌려준다. 이미 실행 중이면 거절한다. */
    public synchronized long start() {
        jobs.findRunningJobId().ifPresent(id -> {
            throw new BatchAlreadyRunningException(id);
        });
        long jobId = jobs.create();
        executor.execute(() -> run(jobId));
        return jobId;
    }

    public BatchJob status(long jobId) {
        return jobs.find(jobId).orElseThrow(() -> new BatchJobNotFoundException(jobId));
    }

    void run(long jobId) {
        int processed = 0;
        int failed = 0;
        String message = null;
        try {
            long lastId = 0;
            while (true) {
                List<Long> ids = members.memberIdsAfter(lastId, chunkSize);
                if (ids.isEmpty()) {
                    break;
                }
                lastId = ids.get(ids.size() - 1);
                try {
                    processed += processChunk(jobId, ids);
                } catch (CallFailedException e) {
                    failed += ids.size();
                    message = e.getMessage();
                    log.warn("chunk 실패 jobId={} firstId={} message={}", jobId, ids.get(0), e.getMessage());
                }
                jobs.updateProgress(jobId, processed, failed);
            }
            jobs.finish(jobId, failed == 0 ? "DONE" : "DONE_WITH_FAILURES", message);
        } catch (RuntimeException e) {
            log.error("배치 비정상 종료 jobId={}", jobId, e);
            jobs.updateProgress(jobId, processed, failed);
            jobs.finish(jobId, "FAILED", e.getMessage());
        }
    }

    /** chunk 하나를 한 트랜잭션으로 처리한다. 저장 중 예외가 나면 이 chunk의 저장만 취소된다. */
    private int processChunk(long jobId, List<Long> ids) {
        SummaryResult result = orchestrator.summarize(new CallContext(ids));
        tx.executeWithoutResult(status -> {
            for (MemberSummary summary : result.summaries()) {
                jobs.upsertSnapshot(summary.id(), summary.grade(),
                        summary.points() == null ? 0 : summary.points(),
                        summary.orderCount() == null ? 0 : summary.orderCount(), jobId);
            }
        });
        return result.summaries().size();
    }
}
