package com.devguna.curriculum.ch12;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

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
    void lifecycleMatchesChapter09Behavior() throws Exception {
        long id = create("life@example.com");

        mockMvc.perform(get("/users/" + id)).andExpect(status().isOk());
        mockMvc.perform(put("/users/" + id).contentType(MediaType.APPLICATION_JSON)
                        .content(body("renamed", "life@example.com", 33)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("renamed"));
        mockMvc.perform(delete("/users/" + id)).andExpect(status().isNoContent());
        mockMvc.perform(get("/users/" + id)).andExpect(status().isNotFound());
        mockMvc.perform(delete("/users/" + id)).andExpect(status().isNotFound());
    }

    @Test
    void duplicateEmailReturns409() throws Exception {
        create("dup@example.com");

        mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON)
                        .content(body("again", "dup@example.com", 21)))
                .andExpect(status().isConflict());
    }

    @Test
    void searchFiltersByNameAndAgeRange() throws Exception {
        mockMvc.perform(get("/users/search").param("name", "e").param("minAge", "30"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items[?(@.name == 'lee')]").exists())
                .andExpect(jsonPath("$.items[?(@.name == 'kim')]").doesNotExist());
    }

    @Test
    void searchReturnsPagingMetadata() throws Exception {
        mockMvc.perform(get("/users/search").param("size", "2"))
                .andExpect(jsonPath("$.items.length()").value(2))
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(2));
    }

    @Test
    void xmlQueryIsExposed() throws Exception {
        mockMvc.perform(get("/users/older-than/40"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("park"));
    }

    @Test
    void sizeOutOfRangeReturns400() throws Exception {
        mockMvc.perform(get("/users/search").param("size", "100")).andExpect(status().isBadRequest());
    }
}
