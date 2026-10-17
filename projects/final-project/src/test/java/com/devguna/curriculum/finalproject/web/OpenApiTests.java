package com.devguna.curriculum.finalproject.web;

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
class OpenApiTests {

    @Autowired
    MockMvc mvc;

    @Test
    void documentListsTheContractedEndpoints() throws Exception {
        mvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.info.title").value("게시판 API"))
                .andExpect(jsonPath("$.paths['/api/v1/posts']").exists())
                .andExpect(jsonPath("$.paths['/api/v1/posts/{id}']").exists())
                .andExpect(jsonPath("$.paths['/api/v1/users']").exists())
                .andExpect(jsonPath("$.paths['/api/v1/admin/stats']").exists())
                .andExpect(jsonPath("$.components.securitySchemes.basicAuth.scheme").value("basic"))
                .andExpect(jsonPath("$.components.schemas.PostRequest").exists())
                .andExpect(jsonPath("$.components.schemas.ErrorBody").doesNotExist());
    }

    @Test
    void pageRoutesAreNotDocumentedAsApi() throws Exception {
        mvc.perform(get("/v3/api-docs"))
                .andExpect(jsonPath("$.paths['/pages/posts']").doesNotExist());
    }
}
