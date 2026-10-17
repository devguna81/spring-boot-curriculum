package com.devguna.curriculum.ch10;

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
class PostApiTests {

    @Autowired
    MockMvc mockMvc;

    @Test
    void firstPageHasPagingMetadata() throws Exception {
        mockMvc.perform(get("/posts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items.length()").value(5))
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(5))
                .andExpect(jsonPath("$.totalElements").value(12))
                .andExpect(jsonPath("$.totalPages").value(3))
                .andExpect(jsonPath("$.items[0].author.name").value("park"));
    }

    @Test
    void lastPageHoldsTheRemainder() throws Exception {
        mockMvc.perform(get("/posts").param("page", "2"))
                .andExpect(jsonPath("$.items.length()").value(2));
    }

    @Test
    void pageBeyondEndIsEmptyButKeepsTotals() throws Exception {
        mockMvc.perform(get("/posts").param("page", "9"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items.length()").value(0))
                .andExpect(jsonPath("$.totalElements").value(12));
    }

    @Test
    void repeatedStatusParameterBecomesList() throws Exception {
        mockMvc.perform(get("/posts").param("status", "PUBLISHED").param("status", "DRAFT").param("size", "50"))
                .andExpect(jsonPath("$.totalElements").value(10));
    }

    @Test
    void searchCombinesKeywordAndStatus() throws Exception {
        mockMvc.perform(get("/posts").param("keyword", "Spring").param("status", "PUBLISHED"))
                .andExpect(jsonPath("$.totalElements").value(3))
                .andExpect(jsonPath("$.totalPages").value(1));
    }

    @Test
    void sizeOutOfRangeReturns400() throws Exception {
        mockMvc.perform(get("/posts").param("size", "0")).andExpect(status().isBadRequest());
        mockMvc.perform(get("/posts").param("size", "51")).andExpect(status().isBadRequest());
        mockMvc.perform(get("/posts").param("page", "-1")).andExpect(status().isBadRequest());
    }

    @Test
    void userWithPostsReturnsNestedList() throws Exception {
        mockMvc.perform(get("/users/2/posts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("lee"))
                .andExpect(jsonPath("$.posts.length()").value(4));
    }

    @Test
    void unknownUserReturns404() throws Exception {
        mockMvc.perform(get("/users/999/posts")).andExpect(status().isNotFound());
    }
}
