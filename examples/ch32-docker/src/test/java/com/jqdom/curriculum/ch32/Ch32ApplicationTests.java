package com.jqdom.curriculum.ch32;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class Ch32ApplicationTests {

    @Autowired
    MockMvc mvc;

    @Test
    void helloAnswers() throws Exception {
        mvc.perform(get("/hello")).andExpect(status().isOk()).andExpect(jsonPath("$.message").value("hello from container"));
    }

    @Test
    void visitsCountUpAcrossRequests() throws Exception {
        String first = mvc.perform(get("/visits")).andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        String second = mvc.perform(get("/visits")).andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

        long a = Long.parseLong(first.replaceAll("\\D", ""));
        long b = Long.parseLong(second.replaceAll("\\D", ""));
        org.assertj.core.api.Assertions.assertThat(b).isEqualTo(a + 1);
    }

    @Test
    void livenessAndReadinessProbesAreUp() throws Exception {
        mvc.perform(get("/actuator/health/liveness")).andExpect(status().isOk()).andExpect(jsonPath("$.status").value("UP"));
        mvc.perform(get("/actuator/health/readiness")).andExpect(status().isOk()).andExpect(jsonPath("$.status").value("UP"));
    }
}
