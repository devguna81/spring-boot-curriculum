package com.jqdom.curriculum.ch03;

import java.time.Clock;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 직접 만들 수 없는 클래스(라이브러리 클래스)는 {@code @Component}를 붙일 수 없으므로
 * {@code @Configuration} 클래스의 {@code @Bean} 메서드로 등록한다. Bean 이름은 메서드 이름(systemClock)이다.
 */
@Configuration
public class AppConfig {

    @Bean
    public Clock systemClock() {
        return Clock.systemUTC();
    }
}
