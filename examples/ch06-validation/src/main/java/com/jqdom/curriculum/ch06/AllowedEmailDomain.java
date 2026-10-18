package com.jqdom.curriculum.ch06;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

/**
 * 직접 만든 제약 애노테이션. 이메일의 도메인이 허용 목록에 있어야 통과한다.
 * 검사 로직은 {@link AllowedEmailDomainValidator}에 있다.
 * 값이 null이면 검사하지 않는다(없는 값은 {@code @NotNull}/{@code @NotBlank}가 따로 책임진다).
 */
@Documented
@Constraint(validatedBy = AllowedEmailDomainValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.RECORD_COMPONENT})
@Retention(RetentionPolicy.RUNTIME)
public @interface AllowedEmailDomain {

    String message() default "허용되지 않는 이메일 도메인입니다";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    String[] value() default {"example.com"};
}
