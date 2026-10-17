package com.devguna.curriculum.ch04;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void listReturnsAllUsers() throws Exception {
        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(4)))
                .andExpect(jsonPath("$[0].name").value("kim"));
    }

    @Test
    void keywordFiltersByName() throws Exception {
        mockMvc.perform(get("/users").param("keyword", "kim"))
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    void limitCutsResult() throws Exception {
        mockMvc.perform(get("/users").param("keyword", "kim").param("limit", "1"))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    void getByIdReturnsUser() throws Exception {
        mockMvc.perform(get("/users/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("lee@example.com"));
    }

    @Test
    void unknownIdReturns404() throws Exception {
        mockMvc.perform(get("/users/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void nonNumericIdReturns400() throws Exception {
        mockMvc.perform(get("/users/abc"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void nonNumericLimitReturns400() throws Exception {
        mockMvc.perform(get("/users").param("limit", "many"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void methodsWithoutMappingReturn405() throws Exception {
        mockMvc.perform(post("/users"))
                .andExpect(status().isMethodNotAllowed())
                .andExpect(header().string("Allow", "GET"));
        mockMvc.perform(delete("/users/1"))
                .andExpect(status().isMethodNotAllowed());
    }
}
