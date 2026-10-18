package com.jqdom.curriculum.ch02;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

/**
 * {@code app.greeting.*} 설정을 묶어서 받는 객체.
 *
 * <p>record는 생성자 바인딩을 사용하므로 값이 한 번 정해지면 바뀌지 않는다.
 * 설정이 없을 때의 기본값은 {@code @DefaultValue}로 지정한다.
 * yml의 {@code max-length}처럼 하이픈으로 쓴 키는 {@code maxLength} 필드에 바인딩된다(relaxed binding).
 */
@ConfigurationProperties(prefix = "app.greeting")
public record GreetingProperties(
        @DefaultValue("Hello") String prefix,
        @DefaultValue("!") String suffix,
        @DefaultValue("20") int maxLength) {
}
