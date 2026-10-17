package com.jqdom.curriculum.ch35.aspect;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** 이 애노테이션이 붙은 메서드의 실행 시간과 성공 여부를 기록한다. */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Measured {
}
