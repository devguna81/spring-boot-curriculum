package com.jqdom.curriculum.ch15;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
class ProductApiTests {

    private static final String BODY = """
            {"name":"laptop","attributes":{"brand":"acme","price":1200,"tags":["new","sale"],
             "specs":{"color":"red","weightKg":1.5}}}""";

    @Autowired
    MockMvc mockMvc;

    @Test
    void postedJsonComesBackThroughTheColumn() throws Exception {
        String location = mockMvc.perform(post("/products").contentType(MediaType.APPLICATION_JSON).content(BODY))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.attributes.brand").value("acme"))
                .andReturn().getResponse().getHeader("Location");

        mockMvc.perform(get(location))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("laptop"))
                .andExpect(jsonPath("$.attributes.tags[1]").value("sale"))
                .andExpect(jsonPath("$.attributes.specs.weightKg").value(1.5));
    }

    @Test
    void unknownProductReturns404() throws Exception {
        mockMvc.perform(get("/products/999999")).andExpect(status().isNotFound());
    }
}
