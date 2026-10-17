package com.jqdom.curriculum.ch18;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class UserContractApiTests {

    private static final AtomicInteger SEQ = new AtomicInteger();

    @Autowired
    MockMvc mockMvc;

    private static String body(String email) {
        return "{\"name\":\"kim\",\"email\":\"" + email + "\",\"age\":28}";
    }

    private static String uniqueEmail() {
        return "user" + SEQ.incrementAndGet() + "@example.com";
    }

    @Test
    void createReturnsOnlyContractFields() throws Exception {
        mockMvc.perform(post("/api/v1/users").contentType(MediaType.APPLICATION_JSON).content(body(uniqueEmail())))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", containsString("/api/v1/users/")))
                .andExpect(jsonPath("$.status").value("ACTIVE"))
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(content().string(not(containsString("internalMemo"))))
                .andExpect(content().string(not(containsString("suspended"))));
    }

    @Test
    void getReturnsCreatedUser() throws Exception {
        String location = mockMvc.perform(post("/api/v1/users").contentType(MediaType.APPLICATION_JSON)
                        .content(body(uniqueEmail())))
                .andReturn().getResponse().getHeader("Location");

        mockMvc.perform(get(location)).andExpect(status().isOk()).andExpect(jsonPath("$.name").value("kim"));
    }

    @Test
    void duplicateEmailReturns409WithCode() throws Exception {
        String email = uniqueEmail();
        mockMvc.perform(post("/api/v1/users").contentType(MediaType.APPLICATION_JSON).content(body(email)));

        mockMvc.perform(post("/api/v1/users").contentType(MediaType.APPLICATION_JSON).content(body(email)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("DUPLICATE_EMAIL"));
    }

    @Test
    void unknownUserReturns404WithCode() throws Exception {
        mockMvc.perform(get("/api/v1/users/999999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("USER_NOT_FOUND"));
    }

    @Test
    void validationFailureListsIssues() throws Exception {
        mockMvc.perform(post("/api/v1/users").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"k\",\"email\":\"bad\",\"age\":-1}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_FAILED"))
                .andExpect(jsonPath("$.issues.length()").value(3));
    }

    @Test
    void oldUnversionedPathDoesNotExist() throws Exception {
        mockMvc.perform(get("/users")).andExpect(status().isNotFound());
    }
}
