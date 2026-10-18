package com.jqdom.curriculum.ch35.aspect;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.stereotype.Component;

/** 기록을 메모리에 모아 둔다. 운영에서는 로그나 지표로 보내지만, 여기서는 테스트로 확인하기 쉽게 목록으로 둔다. */
@Component
public class ExecutionLog {

    private final List<ExecutionRecord> records = new CopyOnWriteArrayList<>();

    void add(ExecutionRecord record) {
        records.add(record);
    }

    public List<ExecutionRecord> records() {
        return List.copyOf(records);
    }

    public void clear() {
        records.clear();
    }
}
