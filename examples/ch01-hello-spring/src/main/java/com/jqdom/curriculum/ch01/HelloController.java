package com.jqdom.curriculum.ch01;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringBootVersion;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 첫 번째 REST API.
 *
 * <p>{@code @RestController}는 반환값을 View 이름이 아니라 HTTP 응답 본문으로 쓴다.
 * 컴포넌트 스캔이 이 클래스를 찾아 Bean으로 등록하고, DispatcherServlet이 요청을 전달한다.
 */
@RestController
public class HelloController {

    private final String applicationName;

    // application.yml의 spring.application.name 값을 주입받는다.
    public HelloController(@Value("${spring.application.name}") String applicationName) {
        this.applicationName = applicationName;
    }

    /** GET /hello, GET /hello?name=devguna */
    @GetMapping("/hello")
    public String hello(@RequestParam(defaultValue = "Spring Boot") String name) {
        return "Hello, " + name + "!";
    }

    /** GET /info - JSON 응답 확인용. 문자열이 아닌 객체를 반환하면 JSON으로 변환된다. */
    @GetMapping("/info")
    public AppInfo info() {
        return new AppInfo(
                applicationName,
                System.getProperty("java.version"),
                SpringBootVersion.getVersion());
    }
}
