package com.jqdom.curriculum.ch02;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

/**
 * {@code @ActiveProfiles}로 Profile을 지정하면 application-{profile}.yml이 application.yml 위에 덮어써진다.
 * 덮어쓰지 않은 키(owner 등)는 기본 설정 값이 유지되는지도 함께 확인한다.
 */
class ProfileTests {

    @Nested
    @SpringBootTest
    @AutoConfigureMockMvc
    @ActiveProfiles("dev")
    class Dev {

        @Autowired
        MockMvc mockMvc;

        @Test
        void overridesPrefixOnly() throws Exception {
            mockMvc.perform(get("/greeting").param("name", "devguna"))
                    .andExpect(status().isOk())
                    .andExpect(content().string("Hi (dev), devguna!"));

            mockMvc.perform(get("/config"))
                    .andExpect(jsonPath("$.activeProfiles[0]").value("dev"))
                    .andExpect(jsonPath("$.owner").value("devguna"))
                    .andExpect(jsonPath("$.greeting.suffix").value("!"))
                    .andExpect(jsonPath("$.greeting.maxLength").value(20));
        }
    }

    @Nested
    @SpringBootTest
    @AutoConfigureMockMvc
    @ActiveProfiles("prod")
    class Prod {

        @Autowired
        MockMvc mockMvc;

        @Test
        void overridesPrefixSuffixAndMaxLength() throws Exception {
            mockMvc.perform(get("/greeting").param("name", "abcdefghijklmnop"))
                    .andExpect(content().string("Welcome, abcdefghij."));

            mockMvc.perform(get("/config"))
                    .andExpect(jsonPath("$.activeProfiles[0]").value("prod"))
                    .andExpect(jsonPath("$.greeting.maxLength").value(10));
        }
    }
}
