package com.jqdom.curriculum.ch30;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
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
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

/** 진짜 필터 체인과 진짜 UserDetailsService를 거치는 보안 테스트. */
@SpringBootTest
@AutoConfigureMockMvc
class SecurityApiTests {

    @Autowired
    MockMvc mvc;

    @Autowired
    UserDetailsService users;

    // ---- 인증(누구인가) ----

    @Test
    void publicEndpointNeedsNoCredentials() throws Exception {
        mvc.perform(get("/api/public/ping")).andExpect(status().isOk());
    }

    @Test
    void protectedEndpointWithoutCredentialsIs401WithJsonBody() throws Exception {
        mvc.perform(get("/api/me"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("UNAUTHORIZED"));
    }

    @Test
    void wrongPasswordIs401() throws Exception {
        mvc.perform(get("/api/me").with(httpBasic("kim", "wrong")))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void correctPasswordIdentifiesTheUser() throws Exception {
        mvc.perform(get("/api/me").with(httpBasic("kim", "kim-pass")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("kim"))
                // 역할(ROLE_USER) 외에 로그인 수단을 나타내는 FACTOR_PASSWORD 권한도 함께 붙어 있다(Spring Security 7에서 관찰).
                // 그래서 roles[0]처럼 위치로 단정하지 않고 포함 여부를 본다.
                .andExpect(jsonPath("$.roles", org.hamcrest.Matchers.hasItems("ROLE_USER", "FACTOR_PASSWORD")));
    }

    @Test
    void passwordsAreStoredAsBcryptHashesNotPlainText() {
        String stored = users.loadUserByUsername("kim").getPassword();

        assertThat(stored).startsWith("{bcrypt}$2");
        assertThat(stored).doesNotContain("kim-pass");
    }

    // ---- 인가(무엇을 할 수 있는가) ----

    @Test
    void userRoleCannotReachAdminArea() throws Exception {
        mvc.perform(get("/api/admin/stats").with(httpBasic("kim", "kim-pass")))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("FORBIDDEN"));
    }

    @Test
    void adminRoleReachesAdminArea() throws Exception {
        mvc.perform(get("/api/admin/stats").with(httpBasic("root", "root-pass")))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "mock", roles = "ADMIN")
    void withMockUserSkipsTheLoginStepAndTestsOnlyAuthorization() throws Exception {
        mvc.perform(get("/api/admin/stats")).andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "mock", roles = "USER")
    void withMockUserWithoutAdminRoleIsForbidden() throws Exception {
        mvc.perform(get("/api/admin/stats")).andExpect(status().isForbidden());
    }

    // ---- 메서드 보안(데이터에 따른 권한) ----

    private long createNoteAs(String user, String pass) throws Exception {
        String json = mvc.perform(post("/api/notes").with(httpBasic(user, pass))
                        .contentType(MediaType.APPLICATION_JSON).content("{\"text\":\"hello\"}"))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return Long.parseLong(json.replaceAll(".*\"id\":(\\d+).*", "$1"));
    }

    @Test
    void ownerCanDeleteOwnNote() throws Exception {
        long id = createNoteAs("kim", "kim-pass");

        mvc.perform(delete("/api/notes/" + id).with(httpBasic("kim", "kim-pass")))
                .andExpect(status().isNoContent());
    }

    @Test
    void anotherUserCannotDeleteIt() throws Exception {
        long id = createNoteAs("kim", "kim-pass");

        mvc.perform(delete("/api/notes/" + id).with(httpBasic("lee", "lee-pass")))
                .andExpect(status().isForbidden());
    }

    @Test
    void adminCanDeleteAnyNote() throws Exception {
        long id = createNoteAs("kim", "kim-pass");

        mvc.perform(delete("/api/notes/" + id).with(httpBasic("root", "root-pass")))
                .andExpect(status().isNoContent());
    }

    @Test
    void notesListShowsOnlyTheCallersOwnNotes() throws Exception {
        createNoteAs("lee", "lee-pass");

        mvc.perform(get("/api/notes").with(httpBasic("kim", "kim-pass")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.owner=='lee')]").isEmpty());
    }

    // ---- 상태 없음(stateless) ----

    @Test
    void noSessionCookieIsIssued() throws Exception {
        mvc.perform(get("/api/me").with(httpBasic("kim", "kim-pass")))
                .andExpect(status().isOk())
                .andExpect(header().doesNotExist("Set-Cookie"));
    }

    @Test
    void postWorksWithoutCsrfTokenBecauseTheApiIsStateless() throws Exception {
        createNoteAs("kim", "kim-pass");
    }
}
