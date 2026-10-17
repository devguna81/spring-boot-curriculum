package com.devguna.curriculum.ch17;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    /** 문서 맨 위에 보이는 제목, 버전, 설명. */
    @Bean
    public OpenAPI openApi() {
        return new OpenAPI().info(new Info()
                .title("Spring Boot Curriculum - Users API")
                .version("v1")
                .description("성공은 success=true와 data, 실패는 success=false와 error로 응답한다."));
    }
}
