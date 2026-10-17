package com.devguna.curriculum.ch02;

import static org.hamcrest.Matchers.empty;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

/** Profile을 지정하지 않으면 application.yml의 값만 적용된다. */
@SpringBootTest
@AutoConfigureMockMvc
class DefaultProfileTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void usesBaseConfiguration() throws Exception {
        mockMvc.perform(get("/greeting").param("name", "devguna"))
                .andExpect(status().isOk())
                .andExpect(content().string("Hello, devguna!"));

        mockMvc.perform(get("/config"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.applicationName").value("ch02-config-profile"))
                .andExpect(jsonPath("$.activeProfiles", empty()))
                .andExpect(jsonPath("$.owner").value("devguna"))
                .andExpect(jsonPath("$.greeting.maxLength").value(20));
    }

    @Test
    void longNameIsCutAtMaxLength() throws Exception {
        mockMvc.perform(get("/greeting").param("name", "abcdefghijklmnopqrstuvwxyz"))
                .andExpect(content().string("Hello, abcdefghijklmnopqrst!"));
    }
}
