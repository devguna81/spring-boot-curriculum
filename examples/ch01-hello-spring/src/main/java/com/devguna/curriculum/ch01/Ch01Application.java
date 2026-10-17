package com.devguna.curriculum.ch01;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 애플리케이션 시작점.
 *
 * <p>{@code @SpringBootApplication}은 아래 세 가지를 한 번에 선언한다.
 * <ul>
 *   <li>{@code @SpringBootConfiguration} - 이 클래스가 설정 클래스임을 표시</li>
 *   <li>{@code @EnableAutoConfiguration} - 클래스패스의 라이브러리를 보고 필요한 설정을 자동 구성</li>
 *   <li>{@code @ComponentScan} - 이 클래스가 속한 패키지 하위에서 Bean 후보를 탐색</li>
 * </ul>
 */
@SpringBootApplication
public class Ch01Application {

    public static void main(String[] args) {
        SpringApplication.run(Ch01Application.class, args);
    }
}
