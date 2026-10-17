package com.jqdom.curriculum.ch22;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * CORS: 화면(localhost:5173)과 API(localhost:8080)는 포트가 달라 "다른 출처"다.
 * 브라우저는 서버가 허용한 출처가 아니면 응답을 스크립트에 넘겨주지 않는다.
 * CORS 는 브라우저가 지키는 규칙이다. Origin 헤더를 붙이지 않는 curl 같은 클라이언트는 막지 못하므로
 * 인증과 인가를 대신하지 않는다.
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    private final String[] allowedOrigins;

    public CorsConfig(@Value("${app.cors.allowed-origins}") String[] allowedOrigins) {
        this.allowedOrigins = allowedOrigins;
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins(allowedOrigins)
                .allowedMethods("GET", "POST", "PUT", "DELETE")
                .allowedHeaders("Content-Type")
                // Location 은 안전 목록 밖의 응답 헤더라, 화면 코드가 읽으려면 노출해야 한다.
                .exposedHeaders("Location")
                .maxAge(3600);
    }
}
