package com.jqdom.curriculum.ch01;

import static org.hamcrest.Matchers.startsWith;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

/** 전체 컨텍스트를 띄우고 MockMvc로 요청/응답을 검증한다. application.yml 값 주입까지 함께 확인된다. */
@SpringBootTest
@AutoConfigureMockMvc
class HelloControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void helloReturnsDefaultGreeting() throws Exception {
        mockMvc.perform(get("/hello"))
                .andExpect(status().isOk())
                .andExpect(content().string("Hello, Spring Boot!"));
    }

    @Test
    void helloReturnsGreetingWithName() throws Exception {
        mockMvc.perform(get("/hello").param("name", "devguna"))
                .andExpect(status().isOk())
                .andExpect(content().string("Hello, devguna!"));
    }

    @Test
    void infoReturnsJson() throws Exception {
        mockMvc.perform(get("/info"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("ch01-hello-spring"))
                .andExpect(jsonPath("$.javaVersion", startsWith("21")))
                .andExpect(jsonPath("$.springBootVersion", startsWith("4.")));
    }
}
