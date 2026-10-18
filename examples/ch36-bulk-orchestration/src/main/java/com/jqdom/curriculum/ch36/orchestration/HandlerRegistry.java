package com.jqdom.curriculum.ch36.orchestration;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

/**
 * 논리 키로 Handler를 찾는 곳. 스프링이 주입한 Handler 목록을 불변 Map으로 만든다.
 * 같은 key가 둘이면 기동이 실패하고, 없는 key를 찾으면 예외가 난다.
 */
@Component
public class HandlerRegistry {

    private final Map<String, CallHandler> handlers;

    public HandlerRegistry(List<CallHandler> handlerList) {
        this.handlers = handlerList.stream()
                .collect(Collectors.toUnmodifiableMap(CallHandler::key, Function.identity()));
    }

    public CallHandler get(String key) {
        CallHandler handler = handlers.get(key);
        if (handler == null) {
            throw new IllegalArgumentException("등록되지 않은 Handler 키입니다: " + key);
        }
        return handler;
    }

    public boolean contains(String key) {
        return handlers.containsKey(key);
    }

    public Set<String> keys() {
        return new TreeSet<>(handlers.keySet());
    }
}
