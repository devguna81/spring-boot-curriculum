package com.devguna.curriculum.finalproject.web;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import com.devguna.curriculum.finalproject.contract.Role;
import com.devguna.curriculum.finalproject.contract.UserView;
import com.devguna.curriculum.finalproject.user.api.UserApi;

import tools.jackson.databind.json.JsonMapper;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class PostApiTests {

    @Autowired
    MockMvc mvc;

    @Autowired
    UserApi userApi;

    @Autowired
    JsonMapper jsonMapper;

    UserView alice;
    UserView bob;
    UserView admin;

    private static final String BODY = "{\"title\":\"제목\",\"content\":\"본문\",\"tags\":[\"a\",\"b\"],\"pinned\":false}";

    @BeforeEach
    void users() {
        alice = userApi.createAccount("alice", "password-1234", "앨리스", Role.USER);
        bob = userApi.createAccount("bob", "password-1234", "밥", Role.USER);
        admin = userApi.createAccount("admin", "password-1234", "관리자", Role.ADMIN);
    }

    private long createAs(UserView user) throws Exception {
        MvcResult result = mvc.perform(post("/api/v1/posts").header("X-User-Id", user.id())
                        .contentType(MediaType.APPLICATION_JSON).content(BODY))
                .andExpect(status().isCreated()).andReturn();
        return jsonMapper.readTree(result.getResponse().getContentAsString()).get("id").asLong();
    }

    @Test
    void createRequiresLogin() throws Exception {
        mvc.perform(post("/api/v1/posts").contentType(MediaType.APPLICATION_JSON).content(BODY))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("UNAUTHORIZED"));
    }

    @Test
    void createReturnsLocationAndBody() throws Exception {
        MvcResult result = mvc.perform(post("/api/v1/posts").header("X-User-Id", alice.id())
                        .contentType(MediaType.APPLICATION_JSON).content(BODY))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.authorName").value("앨리스"))
                .andExpect(jsonPath("$.tags[1]").value("b"))
                .andExpect(jsonPath("$.createdAt").exists())
                .andReturn();

        mvc.perform(get(result.getResponse().getHeader("Location"))).andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("제목"));
    }

    @Test
    void readIsPublic() throws Exception {
        long id = createAs(alice);

        mvc.perform(get("/api/v1/posts/" + id)).andExpect(status().isOk());
        mvc.perform(get("/api/v1/posts")).andExpect(status().isOk()).andExpect(jsonPath("$.total").value(1));
    }

    @Test
    void validationErrorsListFields() throws Exception {
        mvc.perform(post("/api/v1/posts").header("X-User-Id", alice.id()).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"\",\"content\":\"x\",\"tags\":[\"1\",\"2\",\"3\",\"4\",\"5\",\"6\"]}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_FAILED"))
                .andExpect(jsonPath("$.issues.length()").value(2));
    }

    @Test
    void otherUserCannotUpdateOrDelete() throws Exception {
        long id = createAs(alice);

        mvc.perform(put("/api/v1/posts/" + id).header("X-User-Id", bob.id())
                        .contentType(MediaType.APPLICATION_JSON).content(BODY))
                .andExpect(status().isForbidden()).andExpect(jsonPath("$.code").value("FORBIDDEN"));
        mvc.perform(delete("/api/v1/posts/" + id).header("X-User-Id", bob.id())).andExpect(status().isForbidden());
        mvc.perform(get("/api/v1/posts/" + id)).andExpect(status().isOk());
    }

    @Test
    void ownerAndAdminCanUpdateAndDelete() throws Exception {
        long id = createAs(alice);
        String changed = "{\"title\":\"수정\",\"content\":\"본문\",\"tags\":[],\"pinned\":true}";

        mvc.perform(put("/api/v1/posts/" + id).header("X-User-Id", alice.id())
                        .contentType(MediaType.APPLICATION_JSON).content(changed))
                .andExpect(status().isOk()).andExpect(jsonPath("$.title").value("수정"))
                .andExpect(jsonPath("$.pinned").value(true));
        mvc.perform(put("/api/v1/posts/" + id).header("X-User-Id", admin.id())
                        .contentType(MediaType.APPLICATION_JSON).content(BODY))
                .andExpect(status().isOk());
        mvc.perform(delete("/api/v1/posts/" + id).header("X-User-Id", admin.id()))
                .andExpect(status().isNoContent());
        mvc.perform(get("/api/v1/posts/" + id)).andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("NOT_FOUND"));
    }

    @Test
    void unknownPostIsNotFound() throws Exception {
        mvc.perform(get("/api/v1/posts/999999")).andExpect(status().isNotFound());
        mvc.perform(delete("/api/v1/posts/999999").header("X-User-Id", admin.id())).andExpect(status().isNotFound());
    }

    @Test
    void badQueryParametersAreBadRequest() throws Exception {
        mvc.perform(get("/api/v1/posts?size=0")).andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.issues[0].field").value("size"));
        mvc.perform(get("/api/v1/posts?page=abc")).andExpect(status().isBadRequest());
    }

    @Test
    void searchFiltersByTagAndKeyword() throws Exception {
        createAs(alice);

        mvc.perform(get("/api/v1/posts?tag=a&keyword=제목")).andExpect(jsonPath("$.total").value(1));
        mvc.perform(get("/api/v1/posts?tag=zzz")).andExpect(jsonPath("$.total").value(0));
    }

    @Test
    void statsAreAdminOnly() throws Exception {
        createAs(alice);

        mvc.perform(get("/api/v1/admin/stats")).andExpect(status().isUnauthorized());
        mvc.perform(get("/api/v1/admin/stats").header("X-User-Id", alice.id())).andExpect(status().isForbidden());
        mvc.perform(get("/api/v1/admin/stats").header("X-User-Id", admin.id()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].authorName").value("앨리스"))
                .andExpect(jsonPath("$[0].postCount").value(1))
                .andExpect(jsonPath("$.length()").value(3));
    }
}
