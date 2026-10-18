package com.jqdom.curriculum.ch35;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.jqdom.curriculum.ch35.aspect.ExecutionLog;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class AopControllerTests {

    @Autowired
    MockMvc mvc;

    @Autowired
    ExecutionLog log;

    @BeforeEach
    void reset() {
        log.clear();
    }

    @Test
    void greetIsRecordedAndVisibleThroughApi() throws Exception {
        mvc.perform(get("/api/greet").param("name", "Boot"))
                .andExpect(status().isOk())
                .andExpect(content().string("Hello, Boot!"));

        mvc.perform(get("/api/executions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].method").value("GreetingService.greet"))
                .andExpect(jsonPath("$[0].success").value(true));
    }
}
