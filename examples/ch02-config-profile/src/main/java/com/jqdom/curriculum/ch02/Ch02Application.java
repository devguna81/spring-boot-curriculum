package com.jqdom.curriculum.ch02;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

/**
 * {@code @ConfigurationPropertiesScan}은 이 패키지 아래에서 {@code @ConfigurationProperties}가 붙은 클래스를
 * 찾아 Bean으로 등록한다. 이 애노테이션이 없으면 {@code @EnableConfigurationProperties}로 하나씩 등록해야 한다.
 */
@SpringBootApplication
@ConfigurationPropertiesScan
public class Ch02Application {

    public static void main(String[] args) {
        SpringApplication.run(Ch02Application.class, args);
    }
}
