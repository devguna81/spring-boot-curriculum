package com.devguna.curriculum.ch03;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 컴포넌트 스캔의 기준 패키지는 이 클래스가 속한 {@code com.devguna.curriculum.ch03}이다.
 * 이 패키지 밖에 있는 {@code @Component}는 자동으로 등록되지 않는다.
 */
@SpringBootApplication
public class Ch03Application {

    public static void main(String[] args) {
        SpringApplication.run(Ch03Application.class, args);
    }
}
