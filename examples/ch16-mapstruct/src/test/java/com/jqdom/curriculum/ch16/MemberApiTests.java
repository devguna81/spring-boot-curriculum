package com.jqdom.curriculum.ch16;

import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class MemberApiTests {

    private static final String JOIN = """
            {"name":"kim","email":"kimberly@example.com","age":28,"password":"password-1"}""";

    @Autowired
    MockMvc mockMvc;

    private String create() throws Exception {
        return mockMvc.perform(post("/members").contentType(MediaType.APPLICATION_JSON).content(JOIN))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getHeader("Location");
    }

    @Test
    void responseNeverContainsPasswordOrHash() throws Exception {
        mockMvc.perform(post("/members").contentType(MediaType.APPLICATION_JSON).content(JOIN))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.maskedEmail").value("k*******@example.com"))
                .andExpect(jsonPath("$.statusLabel").value("활동"))
                .andExpect(content().string(not(containsString("password"))))
                .andExpect(content().string(not(containsString("kimberly@"))));
    }

    @Test
    void getReturnsTheSameShape() throws Exception {
        String location = create();

        mockMvc.perform(get(location))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("kim"))
                .andExpect(jsonPath("$.joinedDate").isNotEmpty())
                .andExpect(jsonPath("$.passwordHash").doesNotExist());
    }

    @Test
    void patchChangesOnlySentFields() throws Exception {
        String location = create();

        mockMvc.perform(patch(location).contentType(MediaType.APPLICATION_JSON).content("{\"age\":41}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.age").value(41))
                .andExpect(jsonPath("$.name").value("kim"));
    }

    @Test
    void shortPasswordIsRejected() throws Exception {
        mockMvc.perform(post("/members").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"kim\",\"email\":\"k@example.com\",\"age\":28,\"password\":\"short\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void unknownMemberReturns404() throws Exception {
        mockMvc.perform(get("/members/999999")).andExpect(status().isNotFound());
    }
}
