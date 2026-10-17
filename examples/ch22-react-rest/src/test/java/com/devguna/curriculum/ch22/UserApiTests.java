package com.devguna.curriculum.ch22;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
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

@SpringBootTest
@AutoConfigureMockMvc
class UserApiTests {

    static final String ORIGIN = "http://localhost:5173";

    @Autowired
    MockMvc mvc;

    @Autowired
    UserService service;

    private static String json(String name, String email, String age) {
        return "{\"name\":\"" + name + "\",\"email\":\"" + email + "\",\"age\":" + age + "}";
    }

    @Test
    void listReturnsSeedUsers() throws Exception {
        mvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("kim"))
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    void createReturns201WithLocation() throws Exception {
        mvc.perform(post("/api/users").contentType(MediaType.APPLICATION_JSON)
                        .content(json("new1", "new1@example.com", "20")))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", org.hamcrest.Matchers.startsWith("/api/users/")))
                .andExpect(jsonPath("$.email").value("new1@example.com"));
    }

    @Test
    void validationErrorsListEveryField() throws Exception {
        mvc.perform(post("/api/users").contentType(MediaType.APPLICATION_JSON).content(json("k", "bad", "-1")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_FAILED"))
                .andExpect(jsonPath("$.issues", hasSize(3)));
    }

    @Test
    void missingAgeIsReported() throws Exception {
        mvc.perform(post("/api/users").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"kim2\",\"email\":\"kim2@example.com\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.issues[0].field").value("age"));
    }

    @Test
    void malformedJsonIs400() throws Exception {
        mvc.perform(post("/api/users").contentType(MediaType.APPLICATION_JSON).content("{broken"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("MALFORMED_REQUEST"));
    }

    @Test
    void duplicateEmailIs409WithFieldIssue() throws Exception {
        mvc.perform(post("/api/users").contentType(MediaType.APPLICATION_JSON)
                        .content(json("dup", "KIM@example.com", "20")))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("DUPLICATE_EMAIL"))
                .andExpect(jsonPath("$.issues[0].field").value("email"));
    }

    @Test
    void updateChangesUserAndKeepsOwnEmail() throws Exception {
        User created = service.create(new UserRequest("upd", "upd@example.com", 30));
        mvc.perform(put("/api/users/" + created.id()).contentType(MediaType.APPLICATION_JSON)
                        .content(json("upd2", "upd@example.com", "31")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("upd2"))
                .andExpect(jsonPath("$.age").value(31));
    }

    @Test
    void deleteReturns204ThenGetIs404() throws Exception {
        User created = service.create(new UserRequest("del", "del@example.com", 30));
        mvc.perform(delete("/api/users/" + created.id())).andExpect(status().isNoContent());
        mvc.perform(get("/api/users/" + created.id()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("USER_NOT_FOUND"));
    }

    @Test
    void unknownUserUpdateAndDeleteAre404() throws Exception {
        mvc.perform(put("/api/users/9999").contentType(MediaType.APPLICATION_JSON)
                        .content(json("nobody", "nobody@example.com", "20")))
                .andExpect(status().isNotFound());
        mvc.perform(delete("/api/users/9999")).andExpect(status().isNotFound());
    }

    @Test
    void allowedOriginGetsCorsHeaders() throws Exception {
        mvc.perform(get("/api/users").header("Origin", ORIGIN))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", ORIGIN));
    }

    @Test
    void preflightForJsonPostIsAnswered() throws Exception {
        mvc.perform(options("/api/users")
                        .header("Origin", ORIGIN)
                        .header("Access-Control-Request-Method", "POST")
                        .header("Access-Control-Request-Headers", "content-type"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", ORIGIN))
                .andExpect(header().string("Access-Control-Allow-Methods", org.hamcrest.Matchers.containsString("POST")))
                .andExpect(header().string("Access-Control-Max-Age", "3600"));
    }

    @Test
    void otherOriginIsRejectedAtPreflight() throws Exception {
        mvc.perform(options("/api/users")
                        .header("Origin", "http://evil.example")
                        .header("Access-Control-Request-Method", "POST"))
                .andExpect(status().isForbidden())
                .andExpect(header().doesNotExist("Access-Control-Allow-Origin"));
    }

    @Test
    void disallowedMethodIsRejectedAtPreflight() throws Exception {
        mvc.perform(options("/api/users/1")
                        .header("Origin", ORIGIN)
                        .header("Access-Control-Request-Method", "PATCH"))
                .andExpect(status().isForbidden());
    }

    @Test
    void otherOriginGetIsRejectedByServerToo() throws Exception {
        // Spring 은 허용되지 않은 Origin 이 붙은 요청을 사전 요청이 아니어도 403 으로 거절한다.
        mvc.perform(get("/api/users").header("Origin", "http://evil.example"))
                .andExpect(status().isForbidden());
    }

    @Test
    void requestWithoutOriginIsNotAffectedByCors() throws Exception {
        // curl 같은 브라우저 밖 클라이언트는 Origin 을 붙이지 않는다. CORS 는 이런 요청을 막지 못한다.
        mvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(header().doesNotExist("Access-Control-Allow-Origin"));
    }
}
