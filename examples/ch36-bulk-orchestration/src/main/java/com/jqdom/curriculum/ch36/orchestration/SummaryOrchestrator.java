package com.jqdom.curriculum.ch36.orchestration;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 규칙이 정한 순서와 실패 정책대로 Handler를 부른다. 업무 판단은 하지 않는다.
 * 규칙에 적힌 key가 Registry에 없으면 요청이 오기 전에 기동 단계에서 실패한다.
 * 조회성 파이프라인이므로 읽기 전용 트랜잭션에서 실행한다.
 */
@Service
public class SummaryOrchestrator {

    private static final Logger log = LoggerFactory.getLogger(SummaryOrchestrator.class);

    private final List<CallRule> rules;
    private final HandlerRegistry registry;

    public SummaryOrchestrator(CallRuleSource ruleSource, HandlerRegistry registry) {
        this.registry = registry;
        this.rules = ruleSource.rules().stream()
                .sorted(Comparator.comparingInt(CallRule::execSeq))
                .toList();
        for (CallRule rule : rules) {
            if (!registry.contains(rule.handlerKey())) {
                throw new IllegalStateException("규칙이 가리키는 Handler가 없습니다: " + rule.handlerKey()
                        + " (등록된 키: " + registry.keys() + ")");
            }
        }
    }

    @Transactional(readOnly = true)
    public SummaryResult summarize(CallContext context) {
        SummaryDraft draft = new SummaryDraft();
        List<CallOutcome> outcomes = new ArrayList<>();
        List<String> warnings = new ArrayList<>();

        for (CallRule rule : rules) {
            CallHandler handler = registry.get(rule.handlerKey());
            try {
                handler.execute(context, draft);
                outcomes.add(CallOutcome.ok(rule.handlerKey()));
            } catch (RuntimeException e) {
                String message = e.getMessage();
                switch (rule.failPolicy()) {
                    case STOP -> throw new CallFailedException(rule.handlerKey(), e);
                    case CONTINUE -> outcomes.add(CallOutcome.failed(rule.handlerKey(), message));
                    case WARNING -> {
                        outcomes.add(CallOutcome.failed(rule.handlerKey(), message));
                        warnings.add(rule.handlerKey() + " 호출 실패: " + message);
                    }
                    case SKIP -> outcomes.add(CallOutcome.skipped(rule.handlerKey(), message));
                }
                log.warn("Handler 실패 key={} policy={} message={}", rule.handlerKey(), rule.failPolicy(), message);
            }
        }
        return new SummaryResult(
                draft.summaries(context.memberIds()),
                draft.missingIds(context.memberIds()),
                List.copyOf(warnings),
                List.copyOf(outcomes));
    }
}
