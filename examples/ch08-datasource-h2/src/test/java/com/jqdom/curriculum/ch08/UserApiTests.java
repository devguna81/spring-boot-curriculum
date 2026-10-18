package com.jqdom.curriculum.ch08;

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
class UserApiTests {

    @Autowired
    MockMvc mockMvc;

    @Test
    void listReturnsRowsFromDatabase() throws Exception {
        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[0].name").value("kim"));
    }

    @Test
    void unknownIdReturns404() throws Exception {
        mockMvc.perform(get("/users/999")).andExpect(status().isNotFound());
    }

    @Test
    void dbInfoShowsPoolSettings() throws Exception {
        mockMvc.perform(get("/db/info"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dataSourceType").value("HikariDataSource"))
                .andExpect(jsonPath("$.poolName").value("curriculum-pool"))
                .andExpect(jsonPath("$.maximumPoolSize").value(5))
                .andExpect(jsonPath("$.product").value("H2"));
    }
}
