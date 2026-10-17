package com.jqdom.curriculum.ch20;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

/** 테스트마다 새 애플리케이션 컨텍스트를 만들지 않고, 서비스에 남은 데이터를 지워서 독립시킨다. */
@SpringBootTest
@AutoConfigureMockMvc
class UserPageTests {

    @Autowired
    MockMvc mvc;

    @Autowired
    UserService service;

    @BeforeEach
    void clean() {
        service.list().forEach(user -> service.delete(user.id()));
    }

    private MockHttpServletRequestBuilder createRequest(String name, String email, String age) {
        return post("/users").param("name", name).param("email", email).param("age", age);
    }

    @Test
    void emptyFormIsShown() throws Exception {
        mvc.perform(get("/users/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("users/form"))
                .andExpect(content().string(containsString("action=\"/users\"")))
                .andExpect(content().string(containsString("name=\"email\"")));
    }

    @Test
    void createRedirectsAndShowsFlashMessageOnce() throws Exception {
        MvcResult created = mvc.perform(createRequest("kim", "kim@example.com", "28"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/users"))
                .andExpect(flash().attribute("message", "등록했습니다: kim"))
                .andReturn();
        // Flash 속성은 세션에 잠시 저장됐다가 redirect 직후의 요청 한 번에만 전달된다.
        MockHttpSession session = (MockHttpSession) created.getRequest().getSession();
        mvc.perform(get("/users").session(session))
                .andExpect(content().string(containsString("등록했습니다: kim")));
        mvc.perform(get("/users").session(session))
                .andExpect(content().string(not(containsString("등록했습니다"))));
    }

    @Test
    void refreshingTheListAfterCreateDoesNotCreateAgain() throws Exception {
        mvc.perform(createRequest("kim", "kim@example.com", "28")).andExpect(status().is3xxRedirection());
        mvc.perform(get("/users"));
        mvc.perform(get("/users"));
        org.junit.jupiter.api.Assertions.assertEquals(1, service.list().size());
    }

    @Test
    void validationFailureRedisplaysFormWithInputAndErrors() throws Exception {
        mvc.perform(createRequest("k", "bad", "-1"))
                .andExpect(status().isOk())
                .andExpect(view().name("users/form"))
                .andExpect(model().attributeHasFieldErrors("userForm", "name", "email", "age"))
                .andExpect(content().string(containsString("이름은 2자 이상 20자 이하여야 합니다")))
                .andExpect(content().string(containsString("이메일 형식이 올바르지 않습니다")))
                .andExpect(content().string(containsString("나이는 0 이상이어야 합니다")))
                .andExpect(content().string(containsString("value=\"bad\"")));
        org.junit.jupiter.api.Assertions.assertTrue(service.list().isEmpty());
    }

    @Test
    void blankAgeIsReportedAsRequired() throws Exception {
        mvc.perform(createRequest("kim", "kim@example.com", ""))
                .andExpect(model().attributeHasFieldErrors("userForm", "age"))
                .andExpect(content().string(containsString("나이는 필수입니다")));
    }

    @Test
    void nonNumericAgeIsConversionError() throws Exception {
        mvc.perform(createRequest("kim", "kim@example.com", "abc"))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasFieldErrors("userForm", "age"))
                .andExpect(content().string(containsString("value=\"abc\"")))
                .andExpect(content().string(containsString("나이는 숫자로 입력해 주세요")))
                .andExpect(content().string(not(containsString("Failed to convert"))));
    }

    @Test
    void duplicateEmailIsShownAsFieldError() throws Exception {
        service.create(new UserForm("lee", "dup@example.com", 30));
        mvc.perform(createRequest("kim", "DUP@example.com", "28"))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasFieldErrorCode("userForm", "email", "duplicate"))
                .andExpect(content().string(containsString("이미 사용 중인 이메일입니다")));
        org.junit.jupiter.api.Assertions.assertEquals(1, service.list().size());
    }

    @Test
    void inputIsEscapedWhenRedisplayed() throws Exception {
        mvc.perform(createRequest("\"><script>alert(1)</script>", "bad", "1"))
                .andExpect(content().string(not(containsString("<script>alert(1)</script>"))))
                .andExpect(content().string(containsString("&quot;&gt;&lt;script&gt;")));
    }

    @Test
    void editFormIsPrefilled() throws Exception {
        User user = service.create(new UserForm("kim", "kim@example.com", 28));
        mvc.perform(get("/users/" + user.id() + "/edit"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("action=\"/users/" + user.id() + "\"")))
                .andExpect(content().string(containsString("value=\"kim@example.com\"")));
    }

    @Test
    void updateKeepsOwnEmail() throws Exception {
        User user = service.create(new UserForm("kim", "kim@example.com", 28));
        mvc.perform(post("/users/" + user.id()).param("name", "kim2").param("email", "kim@example.com").param("age", "29"))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attribute("message", "수정했습니다: kim2"));
        org.junit.jupiter.api.Assertions.assertEquals("kim2", service.get(user.id()).name());
    }

    @Test
    void updateCannotTakeAnotherUsersEmail() throws Exception {
        service.create(new UserForm("lee", "lee@example.com", 30));
        User kim = service.create(new UserForm("kim", "kim@example.com", 28));
        mvc.perform(post("/users/" + kim.id()).param("name", "kim").param("email", "lee@example.com").param("age", "28"))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasFieldErrorCode("userForm", "email", "duplicate"));
        org.junit.jupiter.api.Assertions.assertEquals("kim@example.com", service.get(kim.id()).email());
    }

    @Test
    void deleteRedirectsAndRemoves() throws Exception {
        User user = service.create(new UserForm("kim", "kim@example.com", 28));
        mvc.perform(post("/users/" + user.id() + "/delete"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/users"));
        org.junit.jupiter.api.Assertions.assertTrue(service.list().isEmpty());
    }

    @Test
    void unknownUserIs404() throws Exception {
        mvc.perform(get("/users/999/edit")).andExpect(status().isNotFound());
        mvc.perform(post("/users/999/delete")).andExpect(status().isNotFound());
    }

    @Test
    void listShowsDeleteFormAsPost() throws Exception {
        User user = service.create(new UserForm("kim", "kim@example.com", 28));
        mvc.perform(get("/users"))
                .andExpect(content().string(containsString("action=\"/users/" + user.id() + "/delete\"")))
                .andExpect(content().string(containsString("method=\"post\"")));
    }
}
