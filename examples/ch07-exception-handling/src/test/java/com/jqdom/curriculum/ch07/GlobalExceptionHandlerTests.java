package com.jqdom.curriculum.ch07;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

/**
 * 오류가 어떤 종류든 응답이 {success:false, error:{code,message,details}} 한 가지 모양인지 확인한다.
 * 저장소가 테스트 사이에 공유되므로 이메일은 호출마다 새로 만든다.
 */
@SpringBootTest
@AutoConfigureMockMvc
class GlobalExceptionHandlerTests {

    private static final AtomicInteger SEQ = new AtomicInteger();

    @Autowired
    private MockMvc mockMvc;

    private static String body(String email) {
        return "{\"name\":\"kim\",\"email\":\"" + email + "\",\"age\":28}";
    }

    private static String uniqueEmail() {
        return "user" + SEQ.incrementAndGet() + "@example.com";
    }

    private long createUser(String email) throws Exception {
        String location = mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON).content(body(email)))
                .andExpect(status().isCreated()).andReturn().getResponse().getHeader("Location");
        return Long.parseLong(location.substring(location.lastIndexOf('/') + 1));
    }

    @Test
    void successIsWrappedInApiResponse() throws Exception {
        mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON).content(body(uniqueEmail())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.name").value("kim"))
                .andExpect(jsonPath("$.error").doesNotExist());
    }

    @Test
    void notFoundBecomes404WithCode() throws Exception {
        mockMvc.perform(get("/users/999999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.code").value("NOT_FOUND"))
                .andExpect(jsonPath("$.data").doesNotExist());
        mockMvc.perform(delete("/users/999999")).andExpect(status().isNotFound());
        mockMvc.perform(put("/users/999999").contentType(MediaType.APPLICATION_JSON).content(body(uniqueEmail())))
                .andExpect(status().isNotFound());
    }

    @Test
    void duplicateEmailBecomes409() throws Exception {
        String email = uniqueEmail();
        createUser(email);

        mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON).content(body(email)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error.code").value("DUPLICATE_EMAIL"));
    }

    @Test
    void updatingToAnotherUsersEmailIsConflictButKeepingOwnEmailIsFine() throws Exception {
        String first = uniqueEmail();
        createUser(first);
        long second = createUser(uniqueEmail());

        mockMvc.perform(patch("/users/" + second).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"" + first + "\"}"))
                .andExpect(status().isConflict());
        mockMvc.perform(put("/users/" + second).contentType(MediaType.APPLICATION_JSON).content(body(uniqueEmail())))
                .andExpect(status().isOk());
    }

    @Test
    void bodyValidationFailureListsEveryField() throws Exception {
        mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"k\",\"email\":\"kim@gmail.com\",\"age\":-1}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.code").value("VALIDATION_FAILED"))
                .andExpect(jsonPath("$.error.details[*].field", containsInAnyOrder("name", "email", "age")));
    }

    @Test
    void parameterValidationFailureNamesTheParameter() throws Exception {
        mockMvc.perform(get("/users").param("limit", "0"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.code").value("VALIDATION_FAILED"))
                .andExpect(jsonPath("$.error.details[0].field").value("limit"));
    }

    @Test
    void malformedJsonBecomes400WithoutParserDetails() throws Exception {
        mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON).content("{not json"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.code").value("MALFORMED_REQUEST"))
                .andExpect(content().string(not(containsString("Unexpected"))));
    }

    @Test
    void typeMismatchBecomes400() throws Exception {
        mockMvc.perform(get("/users/abc"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.code").value("MALFORMED_REQUEST"));
    }

    @Test
    void wrongMethodBecomes405() throws Exception {
        mockMvc.perform(delete("/users"))
                .andExpect(status().isMethodNotAllowed())
                .andExpect(jsonPath("$.error.code").value("METHOD_NOT_ALLOWED"));
    }

    @Test
    void missingContentTypeBecomes415() throws Exception {
        mockMvc.perform(post("/users").content(body(uniqueEmail())))
                .andExpect(status().isUnsupportedMediaType())
                .andExpect(jsonPath("$.error.code").value("UNSUPPORTED_MEDIA_TYPE"));
    }

    @Test
    void unknownPathBecomes404() throws Exception {
        mockMvc.perform(get("/no/such/path"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error.code").value("NOT_FOUND"));
    }

    @Test
    void unexpectedExceptionHidesInternalMessage() throws Exception {
        mockMvc.perform(get("/demo/error"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error.code").value("INTERNAL_ERROR"))
                .andExpect(content().string(not(containsString("hunter2"))));
    }
}
