package com.devguna.curriculum.finalproject.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.devguna.curriculum.finalproject.contract.Role;
import com.devguna.curriculum.finalproject.contract.SignupRequest;
import com.devguna.curriculum.finalproject.contract.UserView;
import com.devguna.curriculum.finalproject.user.api.DuplicateUsernameException;
import com.devguna.curriculum.finalproject.user.api.UserApi;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class UserApiTests {

    @Autowired
    MockMvc mvc;

    @Autowired
    UserApi userApi;

    @Autowired
    PasswordEncoder passwordEncoder;

    private static final String SIGNUP = "{\"username\":\"kim_01\",\"password\":\"secret-1234\",\"displayName\":\"김\"}";

    @Test
    void signupReturnsViewWithoutPassword() throws Exception {
        mvc.perform(post("/api/v1/users").contentType(MediaType.APPLICATION_JSON).content(SIGNUP))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("kim_01"))
                .andExpect(jsonPath("$.role").value("USER"))
                .andExpect(jsonPath("$.password").doesNotExist())
                .andExpect(jsonPath("$.passwordHash").doesNotExist());
    }

    @Test
    void passwordIsStoredAsHash() {
        userApi.signup(new SignupRequest("kim_01", "secret-1234", "김"));

        String hash = userApi.findAccount("kim_01").orElseThrow().passwordHash();

        assertThat(hash).isNotEqualTo("secret-1234").startsWith("{bcrypt}");
        assertThat(passwordEncoder.matches("secret-1234", hash)).isTrue();
        assertThat(passwordEncoder.matches("wrong", hash)).isFalse();
    }

    @Test
    void duplicateUsernameIsConflict() throws Exception {
        userApi.signup(new SignupRequest("kim_01", "secret-1234", "김"));

        assertThatThrownBy(() -> userApi.signup(new SignupRequest("kim_01", "other-pass-1", "박")))
                .isInstanceOf(DuplicateUsernameException.class);
        mvc.perform(post("/api/v1/users").contentType(MediaType.APPLICATION_JSON).content(SIGNUP))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("DUPLICATE_USERNAME"));
    }

    @Test
    void invalidSignupListsFields() throws Exception {
        mvc.perform(post("/api/v1/users").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"A!\",\"password\":\"short\",\"displayName\":\"\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_FAILED"))
                .andExpect(jsonPath("$.issues.length()").value(4));
    }

    @Test
    void malformedJsonIsBadRequest() throws Exception {
        mvc.perform(post("/api/v1/users").contentType(MediaType.APPLICATION_JSON).content("{"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_FAILED"));
    }

    @Test
    void meReturnsCurrentUser() throws Exception {
        UserView user = userApi.createAccount("admin1", "secret-1234", "관리", Role.ADMIN);

        mvc.perform(get("/api/v1/users/me").with(httpBasic("admin1", "secret-1234")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.role").value("ADMIN"));
    }

    @Test
    void meWithoutOrWithWrongCredentialsIsUnauthorized() throws Exception {
        userApi.createAccount("admin1", "secret-1234", "관리", Role.ADMIN);

        mvc.perform(get("/api/v1/users/me"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("UNAUTHORIZED"));
        mvc.perform(get("/api/v1/users/me").with(httpBasic("admin1", "wrong-password"))).andExpect(status().isUnauthorized());
        mvc.perform(get("/api/v1/users/me").with(httpBasic("nobody", "secret-1234"))).andExpect(status().isUnauthorized());
    }

    @Test
    void signupIsOpenToAnonymousUsers() throws Exception {
        mvc.perform(post("/api/v1/users").contentType(MediaType.APPLICATION_JSON).content(SIGNUP))
                .andExpect(status().isCreated());
    }

    @Test
    void newUserCanLogInWithTheChosenPassword() throws Exception {
        mvc.perform(post("/api/v1/users").contentType(MediaType.APPLICATION_JSON).content(SIGNUP))
                .andExpect(status().isCreated());

        mvc.perform(get("/api/v1/users/me").with(httpBasic("kim_01", "secret-1234")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("kim_01"));
    }

    @Test
    void multibytePasswordLongerThan72BytesIsBadRequest() throws Exception {
        // 글자 수는 30자라 @Size(max = 72)를 통과하지만 UTF-8로는 90바이트다. bcrypt는 72바이트까지만 받는다.
        String password = "가".repeat(30);

        mvc.perform(post("/api/v1/users").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"kim_02\",\"password\":\"" + password + "\",\"displayName\":\"김\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.issues[0].field").value("password"));
    }

    @Test
    void multibytePasswordWithin72BytesCanLogIn() throws Exception {
        String password = "가".repeat(24); // 72바이트
        mvc.perform(post("/api/v1/users").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"kim_03\",\"password\":\"" + password + "\",\"displayName\":\"김\"}"))
                .andExpect(status().isCreated());

        mvc.perform(get("/api/v1/users/me").with(httpBasic("kim_03", password))).andExpect(status().isOk());
    }
}
