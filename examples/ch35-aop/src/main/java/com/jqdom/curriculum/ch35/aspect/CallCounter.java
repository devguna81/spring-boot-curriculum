package com.jqdom.curriculum.ch35.aspect;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

/**
 * 애노테이션이 아니라 패키지로 대상을 고르는 예. service 패키지 아래 클래스의 public 메서드 호출 횟수를 센다.
 * 대상 코드는 이 Aspect의 존재를 모른다.
 */
@Aspect
@Component
public class CallCounter {

    private final Map<String, AtomicInteger> counts = new ConcurrentHashMap<>();

    @Before("execution(public * com.jqdom.curriculum.ch35.service..*(..))")
    public void count(JoinPoint joinPoint) {
        String name = joinPoint.getSignature().getDeclaringType().getSimpleName()
                + "." + joinPoint.getSignature().getName();
        counts.computeIfAbsent(name, key -> new AtomicInteger()).incrementAndGet();
    }

    public int countOf(String method) {
        AtomicInteger count = counts.get(method);
        return count == null ? 0 : count.get();
    }

    public void clear() {
        counts.clear();
    }
}
