package com.devguna.curriculum.ch02;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 설정 값을 읽는 두 가지 방법을 한 곳에서 비교한다.
 * <ul>
 *   <li>{@code @Value} - 키 하나를 직접 주입. 기본값은 {@code ${키:기본값}} 형태로 지정</li>
 *   <li>{@code @ConfigurationProperties} - 같은 접두사의 설정을 객체 하나로 묶어 주입</li>
 * </ul>
 */
@RestController
public class ConfigController {

    private final Environment environment;
    private final GreetingProperties greeting;
    private final String owner;

    public ConfigController(
            Environment environment,
            GreetingProperties greeting,
            @Value("${app.owner:unknown}") String owner) {
        this.environment = environment;
        this.greeting = greeting;
        this.owner = owner;
    }

    /** GET /greeting?name=devguna - 설정의 prefix, suffix로 인사말을 만든다. 이름은 maxLength까지만 사용한다. */
    @GetMapping("/greeting")
    public String greeting(@RequestParam(defaultValue = "Spring Boot") String name) {
        String shortName = name.length() > greeting.maxLength()
                ? name.substring(0, greeting.maxLength())
                : name;
        return greeting.prefix() + ", " + shortName + greeting.suffix();
    }

    /** GET /config - 현재 적용된 Profile과 설정 값을 보여 준다. */
    @GetMapping("/config")
    public ConfigView config() {
        List<String> profiles = Arrays.asList(environment.getActiveProfiles());
        return new ConfigView(
                environment.getProperty("spring.application.name"),
                profiles,
                environment.getProperty("server.port", "8080"),
                owner,
                greeting);
    }

    /** /config 응답 본문. */
    public record ConfigView(
            String applicationName,
            List<String> activeProfiles,
            String serverPort,
            String owner,
            GreetingProperties greeting) {
    }
}
