package com.devguna.curriculum.ch09;

import static org.hamcrest.Matchers.endsWith;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

/** 요청은 실제 트랜잭션으로 커밋되므로 각 테스트는 자기 이메일만 사용한다. */
@SpringBootTest
@AutoConfigureMockMvc
class UserApiTests {

    @Autowired
    MockMvc mockMvc;

    private String body(String name, String email, int age) {
        return "{\"name\":\"%s\",\"email\":\"%s\",\"age\":%d}".formatted(name, email, age);
    }

    private long create(String email) throws Exception {
        MvcResult result = mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON)
                .content(body("api", email, 20))).andExpect(status().isCreated()).andReturn();
        String location = result.getResponse().getHeader("Location");
        return Long.parseLong(location.substring(location.lastIndexOf('/') + 1));
    }

    @Test
    void createReturns201WithLocationAndCreatedAt() throws Exception {
        mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON)
                        .content(body("api", "create@example.com", 20)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", org.hamcrest.Matchers.startsWith("/users/")))
                .andExpect(jsonPath("$.email").value("create@example.com"))
                .andExpect(jsonPath("$.createdAt").isNotEmpty());
    }

    @Test
    void getUpdateDeleteLifecycle() throws Exception {
        long id = create("life@example.com");

        mockMvc.perform(get("/users/" + id)).andExpect(status().isOk());
        mockMvc.perform(put("/users/" + id).contentType(MediaType.APPLICATION_JSON)
                        .content(body("renamed", "life@example.com", 33)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("renamed"))
                .andExpect(jsonPath("$.age").value(33));
        mockMvc.perform(delete("/users/" + id)).andExpect(status().isNoContent());
        mockMvc.perform(get("/users/" + id)).andExpect(status().isNotFound());
        mockMvc.perform(delete("/users/" + id)).andExpect(status().isNotFound());
    }

    @Test
    void putOnMissingUserReturns404() throws Exception {
        mockMvc.perform(put("/users/999999").contentType(MediaType.APPLICATION_JSON)
                        .content(body("missing", "missing@example.com", 20)))
                .andExpect(status().isNotFound());
    }

    @Test
    void duplicateEmailReturns409() throws Exception {
        create("dup@example.com");

        mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON)
                        .content(body("again", "dup@example.com", 21)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("DUPLICATE_EMAIL"));
    }

    @Test
    void invalidBodyReturns400() throws Exception {
        mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON)
                        .content(body("k", "not-an-email", -1)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void seededUsersAreListed() throws Exception {
        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("kim"));
        mockMvc.perform(get("/users")).andExpect(status().isOk()).andExpect(jsonPath("$[0].email", endsWith("@example.com")));
    }
}
