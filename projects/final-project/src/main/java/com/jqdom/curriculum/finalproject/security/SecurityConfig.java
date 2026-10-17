package com.jqdom.curriculum.finalproject.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

/**
 * 보안 규칙 전부. 요청은 컨트롤러에 닿기 전에 이 필터 체인을 지난다.
 * 여기서는 "로그인했는가"와 "관리자인가"만 판단하고, "내 글인가"는 서비스가 판단한다.
 */
@Configuration
class SecurityConfig {

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http, JsonErrorHandlers errorHandlers) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        // 가입과 글 읽기는 누구나
                        .requestMatchers(HttpMethod.POST, "/api/v1/users").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/posts", "/api/v1/posts/*").permitAll()
                        // 읽기 전용 서버 렌더링 화면, React 정적 파일, API 문서
                        .requestMatchers("/", "/pages/**", "/app/**", "/css/**").permitAll()
                        .requestMatchers("/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        // 상태 확인만 공개하고 나머지 운영 정보는 관리자만
                        .requestMatchers("/actuator/health", "/actuator/health/**").permitAll()
                        .requestMatchers("/actuator/**").hasRole("ADMIN")
                        .requestMatchers("/api/v1/admin/**").hasRole("ADMIN")
                        // 목록에 없는 요청은 로그인해야 한다. 새 경로를 추가하고 규칙을 깜빡해도 열리지 않는다.
                        .anyRequest().authenticated())
                .httpBasic(basic -> basic.authenticationEntryPoint(errorHandlers))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // 쿠키 세션이 없고 매 요청에 Authorization 헤더를 직접 실어 보내므로 CSRF 토큰을 끈다.
                // 쿠키로 로그인 상태를 유지하는 방식으로 바꾼다면 반드시 켜야 한다.
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .logout(AbstractHttpConfigurer::disable)
                .exceptionHandling(e -> e
                        .authenticationEntryPoint(errorHandlers)
                        .accessDeniedHandler(errorHandlers))
                .headers(Customizer.withDefaults());
        return http.build();
    }
}
