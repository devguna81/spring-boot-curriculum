package com.devguna.curriculum.ch23;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class UserApiTests {

    @Autowired
    MockMvc mvc;

    private static final String JSON = "{\"name\":\"%s\",\"email\":\"%s\"}";

    @Test
    void registerThenGet() throws Exception {
        String location = mvc.perform(post("/api/users").contentType(MediaType.APPLICATION_JSON)
                        .content(JSON.formatted("kim", "api-kim@example.com")))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", org.hamcrest.Matchers.startsWith("/api/users/")))
                .andExpect(jsonPath("$.email").value("api-kim@example.com"))
                .andReturn().getResponse().getHeader("Location");

        // 다른 테스트가 먼저 가입했을 수 있어서 id를 가정하지 않고 Location을 따라간다.
        mvc.perform(get(location))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("kim"));
    }

    @Test
    void duplicateEmailIs409() throws Exception {
        String body = JSON.formatted("lee", "api-lee@example.com");
        mvc.perform(post("/api/users").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated());
        mvc.perform(post("/api/users").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void invalidEmailIs400() throws Exception {
        mvc.perform(post("/api/users").contentType(MediaType.APPLICATION_JSON)
                        .content(JSON.formatted("park", "not-an-email")))
                .andExpect(status().isBadRequest());
    }

    @Test
    void unknownUserIs404() throws Exception {
        mvc.perform(get("/api/users/9999")).andExpect(status().isNotFound());
    }
}
