package com.jqdom.curriculum.ch35;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import com.jqdom.curriculum.ch35.aspect.CallCounter;
import com.jqdom.curriculum.ch35.aspect.ExecutionLog;
import com.jqdom.curriculum.ch35.aspect.ExecutionRecord;
import com.jqdom.curriculum.ch35.service.CallerService;
import com.jqdom.curriculum.ch35.service.GreetingService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class AspectTests {

    @Autowired
    GreetingService greetings;

    @Autowired
    CallerService caller;

    @Autowired
    ExecutionLog log;

    @Autowired
    CallCounter counter;

    @BeforeEach
    void reset() {
        log.clear();
        counter.clear();
    }

    @Test
    void serviceBeanIsAProxy() {
        assertTrue(AopUtils.isAopProxy(greetings), "Aspect가 적용된 Bean은 프록시여야 한다");
    }

    @Test
    void measuredMethodIsRecordedAndResultIsUnchanged() {
        assertEquals("Hello, Boot!", greetings.greet("Boot"));

        List<ExecutionRecord> records = log.records();
        assertEquals(1, records.size());
        assertEquals("GreetingService.greet", records.get(0).method());
        assertTrue(records.get(0).success());
    }

    @Test
    void elapsedTimeIncludesTheMethodBody() throws Exception {
        greetings.slow(60);
        assertTrue(log.records().get(0).elapsedMillis() >= 60);
    }

    @Test
    void failureIsRecordedAndRethrown() {
        assertThrows(IllegalStateException.class, () -> greetings.fail());

        ExecutionRecord record = log.records().get(0);
        assertEquals("GreetingService.fail", record.method());
        assertFalse(record.success());
    }

    @Test
    void methodWithoutAnnotationIsNotMeasured() {
        greetings.plain();
        assertTrue(log.records().isEmpty());
    }

    @Test
    void callThroughAnotherBeanPassesTheProxy() {
        caller.callGreet("A");
        assertEquals(1, log.records().size());
    }

    @Test
    void selfInvocationBypassesTheProxy() {
        greetings.selfCall("A");
        // selfCall은 @Measured가 아니고, 그 안의 this.greet는 프록시를 거치지 않으므로 기록이 없다.
        assertTrue(log.records().isEmpty());
    }

    @Test
    void packagePointcutCountsCallsWithoutAnnotations() {
        greetings.plain();
        greetings.plain();
        greetings.selfCall("A");
        assertEquals(2, counter.countOf("GreetingService.plain"));
        assertEquals(1, counter.countOf("GreetingService.selfCall"));
        // this.greet는 프록시를 거치지 않으므로 패키지 포인트컷에도 잡히지 않는다.
        assertEquals(0, counter.countOf("GreetingService.greet"));
    }
}
