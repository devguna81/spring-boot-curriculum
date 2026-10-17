package com.devguna.curriculum.finalproject.contract;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

/**
 * 문자열이 UTF-8로 인코딩했을 때 지정한 바이트 수를 넘지 않아야 한다.
 * {@code @Size}는 글자 수를 세므로 한글은 글자당 3바이트인 점을 잡아내지 못한다.
 * bcrypt는 비밀번호를 72바이트까지만 받기 때문에 비밀번호 검증에 쓴다.
 */
@Documented
@Constraint(validatedBy = MaxBytesValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.RECORD_COMPONENT})
@Retention(RetentionPolicy.RUNTIME)
public @interface MaxBytes {

    String message() default "UTF-8 기준 {value}바이트를 넘을 수 없습니다 (한글은 글자당 3바이트)";

    int value();

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
