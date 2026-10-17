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

    static final String SCHEME = "userIdHeader";

    /**
     * Swagger UI의 Authorize 버튼에 X-User-Id를 넣을 수 있게 한다.
     * 임시 인증 방식이라 f04에서 HTTP Basic 설명으로 바꾼다.
     */
    @Bean
    OpenAPI openApi() {
        return new OpenAPI()
                .info(new Info().title("게시판 API").version("v1")
                        .description("종합 프로젝트의 사용자·게시글 API. 오류는 모두 ErrorBody 모양이다."))
                .components(new Components().addSecuritySchemes(SCHEME,
                        new SecurityScheme().type(SecurityScheme.Type.APIKEY)
                                .in(SecurityScheme.In.HEADER).name(CurrentActor.HEADER)))
                .addSecurityItem(new SecurityRequirement().addList(SCHEME));
    }
}
