package com.jqdom.curriculum.ch35.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

/**
 * @Measured가 붙은 메서드를 감싸 실행 시간을 잰다.
 * 대상 메서드의 결과와 예외는 그대로 전달하고, 기록만 덧붙인다.
 */
@Aspect
@Component
public class MeasuredAspect {

    private final ExecutionLog log;

    public MeasuredAspect(ExecutionLog log) {
        this.log = log;
    }

    @Around("@annotation(com.jqdom.curriculum.ch35.aspect.Measured)")
    public Object measure(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String name = signature.getDeclaringType().getSimpleName() + "." + signature.getName();
        long start = System.nanoTime();
        boolean success = false;
        try {
            Object result = joinPoint.proceed();
            success = true;
            return result;
        } finally {
            long elapsed = (System.nanoTime() - start) / 1_000_000;
            log.add(new ExecutionRecord(name, elapsed, success));
        }
    }
}
