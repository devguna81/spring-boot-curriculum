package com.jqdom.curriculum.ch30;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

/**
 * 이 예제의 보안 규칙 전부. 요청이 컨트롤러에 닿기 전에 이 필터 체인을 지난다.
 * HTTP Basic + 세션 없음 구성이라 API 클라이언트가 매 요청에 자격 증명을 보낸다.
 */
@Configuration
@EnableMethodSecurity
class SecurityConfig {

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/public/**").permitAll()
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .anyRequest().authenticated())
                .httpBasic(Customizer.withDefaults())
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // 브라우저 쿠키 세션이 없는 API라서 CSRF 토큰을 끈다. 쿠키 세션을 쓰는 화면이라면 켜 두어야 한다.
                .csrf(AbstractHttpConfigurer::disable)
                .exceptionHandling(e -> e
                        .authenticationEntryPoint((req, res, ex) -> writeJson(res, HttpStatus.UNAUTHORIZED, "UNAUTHORIZED"))
                        .accessDeniedHandler((req, res, ex) -> writeJson(res, HttpStatus.FORBIDDEN, "FORBIDDEN")));
        return http.build();
    }

    private static void writeJson(jakarta.servlet.http.HttpServletResponse res, HttpStatus status, String code)
            throws java.io.IOException {
        res.setStatus(status.value());
        res.setContentType(MediaType.APPLICATION_JSON_VALUE);
        res.setCharacterEncoding("UTF-8");
        res.getWriter().write("{\"code\":\"" + code + "\"}");
    }

    /** {bcrypt}, {noop} 같은 접두사로 알고리즘을 구분하는 인코더. 새 비밀번호는 bcrypt로 만든다. */
    @Bean
    PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    /** 학습용 계정. 실제 서비스에서는 DB나 외부 인증 시스템에서 읽는다. 비밀번호를 코드에 두지 않는다. */
    @Bean
    UserDetailsService userDetailsService(PasswordEncoder encoder) {
        return new InMemoryUserDetailsManager(
                User.withUsername("kim").password(encoder.encode("kim-pass")).roles("USER").build(),
                User.withUsername("lee").password(encoder.encode("lee-pass")).roles("USER").build(),
                User.withUsername("root").password(encoder.encode("root-pass")).roles("USER", "ADMIN").build());
    }
}
