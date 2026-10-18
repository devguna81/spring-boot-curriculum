package com.jqdom.curriculum.ch05;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.startsWith;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
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

/**
 * 컨텍스트와 저장소가 테스트 사이에 공유되므로, 각 테스트는 자기가 만든 사용자의 id만 사용한다.
 * JSON은 문자열로 직접 써서 요청 본문이 실제로 어떻게 생겼는지 그대로 보여 준다.
 */
@SpringBootTest
@AutoConfigureMockMvc
class UserApiTests {

    private static final String KIM = """
            {"name":"kim","email":"kim@example.com","age":28}""";

    @Autowired
    private MockMvc mockMvc;

    /** 사용자를 만들고 Location 헤더에서 id를 꺼낸다. */
    private long createUser() throws Exception {
        String location = mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON).content(KIM))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getHeader("Location");
        return Long.parseLong(location.substring(location.lastIndexOf('/') + 1));
    }

    @Test
    void createReturns201WithLocationAndBody() throws Exception {
        mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON).content(KIM))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", startsWith("/users/")))
                .andExpect(jsonPath("$.name").value("kim"))
                .andExpect(jsonPath("$.id").isNumber());
    }

    @Test
    void createdUserAppearsInListAndCanBeFetched() throws Exception {
        long id = createUser();

        mockMvc.perform(get("/users")).andExpect(jsonPath("$[*].id", hasItem((int) id)));
        mockMvc.perform(get("/users/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("kim@example.com"));
    }

    @Test
    void putReplacesWholeResourceAndIsIdempotent() throws Exception {
        long id = createUser();
        String body = """
                {"name":"lee","email":"lee@example.com","age":35}""";

        for (int i = 0; i < 2; i++) {
            mockMvc.perform(put("/users/" + id).contentType(MediaType.APPLICATION_JSON).content(body))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.name").value("lee"))
                    .andExpect(jsonPath("$.age").value(35));
        }
    }

    @Test
    void patchChangesOnlySentFields() throws Exception {
        long id = createUser();

        mockMvc.perform(patch("/users/" + id).contentType(MediaType.APPLICATION_JSON).content("""
                        {"age":29}"""))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("kim"))
                .andExpect(jsonPath("$.age").value(29));
    }

    @Test
    void deleteReturns204ThenNotFound() throws Exception {
        long id = createUser();

        mockMvc.perform(delete("/users/" + id)).andExpect(status().isNoContent());
        mockMvc.perform(get("/users/" + id)).andExpect(status().isNotFound());
        mockMvc.perform(delete("/users/" + id)).andExpect(status().isNotFound());
    }

    @Test
    void putAndPatchOnMissingUserReturn404() throws Exception {
        mockMvc.perform(put("/users/999999").contentType(MediaType.APPLICATION_JSON).content(KIM))
                .andExpect(status().isNotFound());
        mockMvc.perform(patch("/users/999999").contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isNotFound());
    }

    @Test
    void missingContentTypeReturns415() throws Exception {
        mockMvc.perform(post("/users").content(KIM))
                .andExpect(status().isUnsupportedMediaType());
    }

    @Test
    void malformedJsonReturns400() throws Exception {
        mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON).content("{not json"))
                .andExpect(status().isBadRequest());
    }
}
