package com.devguna.curriculum.finalproject.web;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

@Configuration
class OpenApiConfig {

    static final String SCHEME = "basicAuth";

    /** Swagger UI의 Authorize 버튼에 아이디와 비밀번호를 넣으면 요청마다 HTTP Basic 헤더가 붙는다. */
    @Bean
    OpenAPI openApi() {
        return new OpenAPI()
                .info(new Info().title("게시판 API").version("v1")
                        .description("종합 프로젝트의 사용자·게시글 API. 오류는 모두 ErrorBody 모양이다."))
                .components(new Components().addSecuritySchemes(SCHEME,
                        new SecurityScheme().type(SecurityScheme.Type.HTTP).scheme("basic")))
                .addSecurityItem(new SecurityRequirement().addList(SCHEME));
    }
}
