package com.devguna.curriculum.finalproject.web;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
class WebConfig implements WebMvcConfigurer {

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        // 주소창에 / 나 /app 만 입력해도 화면이 열리게 한다. React 빌드 결과는 static/app 에 들어간다.
        registry.addRedirectViewController("/", "/app/index.html");
        registry.addRedirectViewController("/app", "/app/index.html");
        registry.addRedirectViewController("/app/", "/app/index.html");
    }
}
