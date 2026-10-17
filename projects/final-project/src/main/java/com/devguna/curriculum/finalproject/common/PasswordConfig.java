package com.devguna.curriculum.finalproject.common;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
class PasswordConfig {

    /** {bcrypt} 같은 접두사로 알고리즘을 구분한다. 새 비밀번호는 bcrypt로 만든다(ch30). */
    @Bean
    PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}
