package com.jqdom.curriculum.ch36.batch;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/batch/member-summaries")
class BatchController {

    private final SummaryBatchRunner runner;

    BatchController(SummaryBatchRunner runner) {
        this.runner = runner;
    }

    /** 실행을 시작하고 작업 번호를 돌려준다(202). 진행 상황은 GET으로 확인한다. */
    @PostMapping
    ResponseEntity<Map<String, Long>> start() {
        return ResponseEntity.accepted().body(Map.of("jobId", runner.start()));
    }

    @GetMapping("/{jobId}")
    BatchJob status(@PathVariable long jobId) {
        return runner.status(jobId);
    }
}
