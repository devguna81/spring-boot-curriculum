package com.jqdom.curriculum.ch36;

import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.jqdom.curriculum.ch36.support.FlakyModules;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

/** reward 모듈이 실패해도 CONTINUE 정책이므로 요약은 나가고, 실패한 항목만 null과 FAILED로 드러난다. */
@SpringBootTest
@AutoConfigureMockMvc
@Import(FlakyModules.class)
class FailurePolicyApiTests {

    @Autowired
    MockMvc mvc;

    @Test
    void rewardFailureDoesNotStopTheSummary() throws Exception {
        mvc.perform(post("/api/members/summaries").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"memberIds\":[1]}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.summaries[0].grade").value("SENIOR"))
                .andExpect(jsonPath("$.summaries[0].points").value(nullValue()))
                .andExpect(jsonPath("$.summaries[0].orderCount").value(3))
                .andExpect(jsonPath("$.outcomes[1].key").value("reward"))
                .andExpect(jsonPath("$.outcomes[1].status").value("FAILED"))
                .andExpect(jsonPath("$.outcomes[1].message").value("reward 모듈 장애"));
    }
}
