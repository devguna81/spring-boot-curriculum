package com.devguna.curriculum.ch28;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.ApplicationContext;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

/**
 * 웹 계층 슬라이스. 컨트롤러와 MVC 설정만 띄우고 UserService는 목으로 바꾼다.
 * 서비스와 매퍼, DB는 만들어지지 않는다.
 */
@WebMvcTest(UserController.class)
class UserControllerWebTests {

    @Autowired
    MockMvc mvc;

    @Autowired
    ApplicationContext context;

    @MockitoBean
    UserService service;

    private static User user(long id, String name) {
        User u = new User(name, name + "@example.com", 30);
        u.setId(id);
        u.setCreatedAt(LocalDateTime.parse("2026-10-17T09:00:00"));
        return u;
    }

    private static final String BODY = "{\"name\":\"kim\",\"email\":\"kim@example.com\",\"age\":28}";

    @Test
    void sliceContainsTheControllerButNotTheMapper() {
        assertThat(context.getBeanNamesForType(UserController.class)).hasSize(1);
        assertThat(context.getBeanNamesForType(UserMapper.class)).isEmpty();
    }

    @Test
    void listReturnsServiceResultAsJson() throws Exception {
        given(service.findAll()).willReturn(List.of(user(1, "kim"), user(2, "lee")));

        mvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[1].name").value("lee"));
    }

    @Test
    void getMissingUserIs404() throws Exception {
        given(service.findById(9)).willReturn(Optional.empty());

        mvc.perform(get("/users/9")).andExpect(status().isNotFound());
    }

    @Test
    void createReturns201WithLocation() throws Exception {
        given(service.create(any())).willReturn(user(5, "kim"));

        mvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON).content(BODY))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/users/5"))
                .andExpect(jsonPath("$.id").value(5));
    }

    @Test
    void invalidBodyIs400AndServiceIsNeverCalled() throws Exception {
        mvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"k\",\"email\":\"bad\",\"age\":-1}"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(service);
    }

    @Test
    void duplicateKeyFromServiceBecomes409ByTheControllerAdvice() throws Exception {
        given(service.create(any())).willThrow(new DuplicateKeyException("unique"));

        mvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON).content(BODY))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("DUPLICATE_EMAIL"));
    }

    @Test
    void deleteMapsServiceBooleanToStatus() throws Exception {
        given(service.delete(1)).willReturn(true);
        given(service.delete(2)).willReturn(false);

        mvc.perform(delete("/users/1")).andExpect(status().isNoContent());
        mvc.perform(delete("/users/2")).andExpect(status().isNotFound());
    }
}
