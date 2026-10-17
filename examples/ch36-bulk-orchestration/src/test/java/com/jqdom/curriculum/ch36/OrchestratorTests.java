package com.jqdom.curriculum.ch36;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import com.jqdom.curriculum.ch36.member.api.MemberGrade;
import com.jqdom.curriculum.ch36.orchestration.CallContext;
import com.jqdom.curriculum.ch36.orchestration.CallFailedException;
import com.jqdom.curriculum.ch36.orchestration.CallHandler;
import com.jqdom.curriculum.ch36.orchestration.CallRule;
import com.jqdom.curriculum.ch36.orchestration.FailPolicy;
import com.jqdom.curriculum.ch36.orchestration.HandlerRegistry;
import com.jqdom.curriculum.ch36.orchestration.SummaryDraft;
import com.jqdom.curriculum.ch36.orchestration.SummaryOrchestrator;
import com.jqdom.curriculum.ch36.orchestration.SummaryResult;

import org.junit.jupiter.api.Test;

/** 스프링 없이 가짜 Handler로 호출 순서와 실패 정책만 확인한다(ch25의 가짜 서비스 방식). */
class OrchestratorTests {

    private final List<String> calls = new ArrayList<>();

    private CallHandler handler(String key, Consumer<SummaryDraft> action) {
        return new CallHandler() {
            @Override
            public String key() {
                return key;
            }

            @Override
            public void execute(CallContext context, SummaryDraft draft) {
                calls.add(key + ":" + context.memberIds());
                action.accept(draft);
            }
        };
    }

    private CallHandler failing(String key) {
        return handler(key, draft -> {
            throw new IllegalStateException(key + " 장애");
        });
    }

    private SummaryOrchestrator orchestrator(List<CallRule> rules, CallHandler... handlers) {
        return new SummaryOrchestrator(() -> rules, new HandlerRegistry(List.of(handlers)));
    }

    private final CallHandler member = handler("member",
            draft -> draft.putMembers(Map.of(1L, new MemberGrade(1, "kim", "SENIOR"))));

    @Test
    void handlersRunInExecSeqOrderNotListOrder() {
        SummaryOrchestrator o = orchestrator(
                List.of(new CallRule("order", 3, FailPolicy.WARNING),
                        new CallRule("member", 1, FailPolicy.STOP),
                        new CallRule("reward", 2, FailPolicy.CONTINUE)),
                member, handler("reward", d -> { }), handler("order", d -> { }));

        o.summarize(new CallContext(List.of(1L)));

        assertEquals(List.of("member:[1]", "reward:[1]", "order:[1]"), calls);
    }

    @Test
    void sameIdListIsPassedToEveryHandlerOnce() {
        SummaryOrchestrator o = orchestrator(
                List.of(new CallRule("member", 1, FailPolicy.STOP), new CallRule("reward", 2, FailPolicy.STOP)),
                member, handler("reward", d -> { }));

        o.summarize(new CallContext(List.of(1L, 1L, 2L)));

        // 중복은 맥락을 만들 때 제거되고, 모듈마다 한 번씩만 호출된다(id마다 호출하지 않는다).
        assertEquals(List.of("member:[1, 2]", "reward:[1, 2]"), calls);
    }

    @Test
    void stopPolicyAbortsAndLaterHandlersAreNotCalled() {
        SummaryOrchestrator o = orchestrator(
                List.of(new CallRule("member", 1, FailPolicy.STOP), new CallRule("reward", 2, FailPolicy.CONTINUE)),
                failing("member"), handler("reward", d -> { }));

        CallFailedException e = assertThrows(CallFailedException.class,
                () -> o.summarize(new CallContext(List.of(1L))));

        assertEquals("member", e.handlerKey());
        assertEquals(List.of("member:[1]"), calls);
    }

    @Test
    void continuePolicyRecordsFailureAndGoesOn() {
        SummaryOrchestrator o = orchestrator(
                List.of(new CallRule("member", 1, FailPolicy.STOP), new CallRule("reward", 2, FailPolicy.CONTINUE),
                        new CallRule("order", 3, FailPolicy.STOP)),
                member, failing("reward"), handler("order", d -> d.putOrderCounts(Map.of(1L, 4L))));

        SummaryResult result = o.summarize(new CallContext(List.of(1L)));

        assertEquals("FAILED", result.outcomes().get(1).status());
        assertTrue(result.warnings().isEmpty());
        assertEquals(null, result.summaries().get(0).points());
        assertEquals(4L, result.summaries().get(0).orderCount());
    }

    @Test
    void warningPolicyAlsoLeavesAWarning() {
        SummaryOrchestrator o = orchestrator(
                List.of(new CallRule("member", 1, FailPolicy.STOP), new CallRule("order", 2, FailPolicy.WARNING)),
                member, failing("order"));

        SummaryResult result = o.summarize(new CallContext(List.of(1L)));

        assertEquals(List.of("order 호출 실패: order 장애"), result.warnings());
    }

    @Test
    void skipPolicyRecordsSkippedNotFailed() {
        SummaryOrchestrator o = orchestrator(
                List.of(new CallRule("member", 1, FailPolicy.STOP), new CallRule("order", 2, FailPolicy.SKIP)),
                member, failing("order"));

        SummaryResult result = o.summarize(new CallContext(List.of(1L)));

        assertEquals("SKIPPED", result.outcomes().get(1).status());
        assertTrue(result.warnings().isEmpty());
    }

    @Test
    void missingIdsAreReportedNotThrown() {
        SummaryOrchestrator o = orchestrator(List.of(new CallRule("member", 1, FailPolicy.STOP)), member);

        SummaryResult result = o.summarize(new CallContext(List.of(1L, 99L)));

        assertEquals(1, result.summaries().size());
        assertEquals(List.of(99L), result.missingIds());
    }

    @Test
    void ruleWithUnknownKeyFailsAtConstruction() {
        IllegalStateException e = assertThrows(IllegalStateException.class,
                () -> orchestrator(List.of(new CallRule("shipping", 1, FailPolicy.STOP)), member));
        assertEquals("규칙이 가리키는 Handler가 없습니다: shipping (등록된 키: [member])", e.getMessage());
    }

    @Test
    void registryRejectsDuplicateKeysAndUnknownLookups() {
        assertThrows(IllegalStateException.class,
                () -> new HandlerRegistry(List.of(handler("member", d -> { }), handler("member", d -> { }))));
        HandlerRegistry registry = new HandlerRegistry(List.of(member));
        assertThrows(IllegalArgumentException.class, () -> registry.get("nothing"));
    }
}
