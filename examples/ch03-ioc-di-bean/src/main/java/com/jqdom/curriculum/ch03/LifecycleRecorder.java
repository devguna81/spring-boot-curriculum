package com.jqdom.curriculum.ch03;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

import org.springframework.stereotype.Component;

/** Bean 생성 직후와 소멸 직전에 호출되는 메서드를 기록한다. */
@Component
public class LifecycleRecorder {

    private final List<String> events = new ArrayList<>();

    public LifecycleRecorder() {
        events.add("constructor");
    }

    @PostConstruct
    void init() {
        events.add("postConstruct");
    }

    @PreDestroy
    void destroy() {
        events.add("preDestroy");
    }

    public List<String> events() {
        return Collections.unmodifiableList(events);
    }
}
