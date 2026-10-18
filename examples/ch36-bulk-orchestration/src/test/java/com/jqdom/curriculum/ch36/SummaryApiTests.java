package com.jqdom.curriculum.ch36;

import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.stream.Collectors;
import java.util.stream.LongStream;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@SpringBootTest
@AutoConfigureMockMvc
class SummaryApiTests {

    @Autowired
    MockMvc mvc;

    private ResultActions postJson(String json) throws Exception {
        return mvc.perform(post("/api/members/summaries").contentType(MediaType.APPLICATION_JSON).content(json));
    }

    @Test
    void summarizesSeveralMembersThroughThreeModules() throws Exception {
        postJson("{\"memberIds\":[1,2,3,99]}")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.summaries.length()").value(3))
                .andExpect(jsonPath("$.summaries[0].id").value(1))
                .andExpect(jsonPath("$.summaries[0].name").value("kim"))
                .andExpect(jsonPath("$.summaries[0].grade").value("SENIOR"))
                .andExpect(jsonPath("$.summaries[0].points").value(600))
                .andExpect(jsonPath("$.summaries[0].orderCount").value(3))
                .andExpect(jsonPath("$.summaries[2].grade").value("DORMANT"))
                .andExpect(jsonPath("$.summaries[2].points").value(0))
                .andExpect(jsonPath("$.missingIds[0]").value(99))
                .andExpect(jsonPath("$.outcomes.length()").value(3))
                .andExpect(jsonPath("$.warnings.length()").value(0));
    }

    @Test
    void duplicateIdsAppearOnce() throws Exception {
        postJson("{\"memberIds\":[2,2,2]}")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.summaries.length()").value(1));
    }

    @Test
    void emptyListIs400() throws Exception {
        postJson("{\"memberIds\":[]}")
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.issues", hasItem("memberIds는 비어 있을 수 없습니다.")));
    }

    @Test
    void nullElementAndNonPositiveAre400() throws Exception {
        postJson("{\"memberIds\":[1,null]}")
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.issues", hasItem("memberIds에 null이 있습니다.")));
        postJson("{\"memberIds\":[0]}")
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.issues", hasItem("memberIds는 양수여야 합니다.")));
    }

    @Test
    void moreThanLimitIs400() throws Exception {
        String ids = LongStream.rangeClosed(1, 1001).mapToObj(String::valueOf).collect(Collectors.joining(","));
        postJson("{\"memberIds\":[" + ids + "]}")
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.issues", hasItem("memberIds는 최대 1000개까지 보낼 수 있습니다.")));
    }

    @Test
    void exactlyLimitIsAccepted() throws Exception {
        String ids = LongStream.rangeClosed(1, 1000).mapToObj(String::valueOf).collect(Collectors.joining(","));
        postJson("{\"memberIds\":[" + ids + "]}")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.summaries.length()").value(5))
                .andExpect(jsonPath("$.missingIds.length()").value(995));
    }
}
