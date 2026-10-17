package com.jqdom.curriculum.ch03;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/** 스프링 부트 없이 컨테이너만 직접 만들어 Scope와 생명주기를 확인한다. */
class ScopeAndLifecycleTests {

    @Component
    static class SingletonBean {
    }

    @Component
    @Scope("prototype")
    static class PrototypeBean {
    }

    @Test
    void singletonReturnsSameInstancePrototypeReturnsNewOne() {
        try (var ctx = new AnnotationConfigApplicationContext(SingletonBean.class, PrototypeBean.class)) {
            assertSame(ctx.getBean(SingletonBean.class), ctx.getBean(SingletonBean.class));
            assertNotSame(ctx.getBean(PrototypeBean.class), ctx.getBean(PrototypeBean.class));
        }
    }

    @Test
    void preDestroyRunsWhenContextCloses() {
        var ctx = new AnnotationConfigApplicationContext(LifecycleRecorder.class);
        LifecycleRecorder recorder = ctx.getBean(LifecycleRecorder.class);
        assertEquals(List.of("constructor", "postConstruct"), recorder.events());

        ctx.close();

        assertEquals(List.of("constructor", "postConstruct", "preDestroy"), recorder.events());
    }
}
