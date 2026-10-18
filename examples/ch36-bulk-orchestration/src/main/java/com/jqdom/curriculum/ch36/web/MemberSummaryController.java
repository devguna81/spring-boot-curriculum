package com.jqdom.curriculum.ch36.web;

import com.jqdom.curriculum.ch36.orchestration.CallContext;
import com.jqdom.curriculum.ch36.orchestration.SummaryOrchestrator;
import com.jqdom.curriculum.ch36.orchestration.SummaryResult;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** 입구. 검증하고 공통 맥락을 만들어 Orchestrator에 넘길 뿐, 어떤 모듈이 불리는지 모른다. */
@RestController
@RequestMapping("/api/members")
class MemberSummaryController {

    private final SummaryOrchestrator orchestrator;

    MemberSummaryController(SummaryOrchestrator orchestrator) {
        this.orchestrator = orchestrator;
    }

    @PostMapping("/summaries")
    SummaryResult summaries(@Valid @RequestBody SummaryRequest request) {
        return orchestrator.summarize(new CallContext(request.memberIds()));
    }
}
