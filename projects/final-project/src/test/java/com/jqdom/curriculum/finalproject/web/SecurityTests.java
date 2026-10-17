package com.jqdom.curriculum.finalproject.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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

import com.jqdom.curriculum.finalproject.contract.Role;
import com.jqdom.curriculum.finalproject.user.api.UserApi;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class SecurityTests {

    @Autowired
    MockMvc mvc;

    @Autowired
    UserApi userApi;

    @BeforeEach
    void users() {
        userApi.createAccount("alice", "password-1234", "앨리스", Role.USER);
        userApi.createAccount("admin", "password-1234", "관리자", Role.ADMIN);
    }

    @Test
    void publicPathsNeedNoLogin() throws Exception {
        mvc.perform(get("/api/v1/posts")).andExpect(status().isOk());
        mvc.perform(get("/pages/posts")).andExpect(status().isOk());
        mvc.perform(get("/v3/api-docs")).andExpect(status().isOk());
        mvc.perform(get("/actuator/health")).andExpect(status().isOk()).andExpect(jsonPath("$.status").value("UP"));
        mvc.perform(get("/actuator/health/readiness")).andExpect(status().isOk());
        mvc.perform(get("/actuator/health/liveness")).andExpect(status().isOk());
    }

    @Test
    void writingRequiresLogin() throws Exception {
        String body = "{\"title\":\"t\",\"content\":\"c\"}";

        mvc.perform(post("/api/v1/posts").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("UNAUTHORIZED"));
        mvc.perform(delete("/api/v1/posts/1")).andExpect(status().isUnauthorized());
    }

    @Test
    void unauthorizedResponseDoesNotAskBrowserForCredentials() throws Exception {
        MvcResult result = mvc.perform(get("/api/v1/users/me")).andExpect(status().isUnauthorized()).andReturn();

        assertThat(result.getResponse().getHeader("WWW-Authenticate")).isNull();
    }

    @Test
    void unknownUserAndWrongPasswordLookTheSame() throws Exception {
        String unknown = mvc.perform(get("/api/v1/users/me").with(httpBasic("nobody", "password-1234")))
                .andExpect(status().isUnauthorized()).andReturn().getResponse().getContentAsString();
        String wrong = mvc.perform(get("/api/v1/users/me").with(httpBasic("alice", "wrong-password")))
                .andExpect(status().isUnauthorized()).andReturn().getResponse().getContentAsString();

        // 응답이 다르면 어떤 아이디가 존재하는지 알아낼 수 있다.
        assertThat(unknown).isEqualTo(wrong);
    }

    @Test
    void adminPathsRequireAdminRole() throws Exception {
        mvc.perform(get("/api/v1/admin/stats")).andExpect(status().isUnauthorized());
        mvc.perform(get("/api/v1/admin/stats").with(httpBasic("alice", "password-1234")))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("FORBIDDEN"));
        mvc.perform(get("/api/v1/admin/stats").with(httpBasic("admin", "password-1234"))).andExpect(status().isOk());
    }

    @Test
    void operationalEndpointsAreAdminOnly() throws Exception {
        mvc.perform(get("/actuator/metrics")).andExpect(status().isUnauthorized());
        mvc.perform(get("/actuator/metrics").with(httpBasic("alice", "password-1234"))).andExpect(status().isForbidden());
        mvc.perform(get("/actuator/metrics").with(httpBasic("admin", "password-1234"))).andExpect(status().isOk());
    }

    @Test
    void unexposedActuatorEndpointsStayClosedEvenForAdmin() throws Exception {
        mvc.perform(get("/actuator/env").with(httpBasic("admin", "password-1234"))).andExpect(status().isNotFound());
        mvc.perform(get("/actuator/beans").with(httpBasic("admin", "password-1234"))).andExpect(status().isNotFound());
    }

    @Test
    void healthDetailsAreHiddenFromAnonymousButShownToAdmin() throws Exception {
        mvc.perform(get("/actuator/health")).andExpect(jsonPath("$.components").doesNotExist());
        mvc.perform(get("/actuator/health").with(httpBasic("admin", "password-1234")))
                .andExpect(jsonPath("$.components.db.status").value("UP"));
    }

    @Test
    void unlistedPathsRequireLogin() throws Exception {
        // 규칙에 없는 경로는 기본적으로 로그인해야 한다. 없는 경로여도 먼저 401을 받는다.
        mvc.perform(get("/internal/anything")).andExpect(status().isUnauthorized());
    }

    @Test
    void responsesCarryBasicSecurityHeaders() throws Exception {
        mvc.perform(get("/api/v1/posts"))
                .andExpect(header().string("X-Content-Type-Options", "nosniff"))
                .andExpect(header().string("X-Frame-Options", "DENY"))
                .andExpect(header().string("Cache-Control", org.hamcrest.Matchers.containsString("no-store")));
    }

    @Test
    void passwordHashNeverAppearsInResponses() throws Exception {
        String body = mvc.perform(get("/api/v1/users/me").with(httpBasic("alice", "password-1234")))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

        assertThat(body).doesNotContain("bcrypt").doesNotContain("password");
    }
}
