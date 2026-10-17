package com.devguna.curriculum.ch19;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class UserPageTests {

    @Autowired
    MockMvc mvc;

    @Test
    void rootRedirectsToList() throws Exception {
        mvc.perform(get("/")).andExpect(status().is3xxRedirection()).andExpect(redirectedUrl("/users"));
    }

    @Test
    void listUsesViewAndModel() throws Exception {
        mvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(view().name("users/list"))
                .andExpect(model().attribute("users", hasSize(4)))
                .andExpect(model().attribute("activeOnly", false));
    }

    @Test
    void listRendersRowsWithCountAndLinks() throws Exception {
        mvc.perform(get("/users"))
                .andExpect(content().string(containsString("kim@example.com")))
                .andExpect(content().string(containsString("href=\"/users/2\"")))
                .andExpect(content().string(containsString("총 <span>4</span>명")));
    }

    @Test
    void inactiveUserGetsClassAndLabel() throws Exception {
        mvc.perform(get("/users"))
                .andExpect(content().string(containsString("class=\"inactive\"")))
                .andExpect(content().string(containsString("비활성")));
    }

    @Test
    void activeOnlyFiltersAndShowsNotice() throws Exception {
        mvc.perform(get("/users").param("activeOnly", "true"))
                .andExpect(model().attribute("users", hasSize(3)))
                .andExpect(content().string(containsString("활성 사용자만 보는 중입니다.")))
                .andExpect(content().string(not(containsString("park@example.com"))));
    }

    @Test
    void navigationFragmentIsIncluded() throws Exception {
        mvc.perform(get("/users"))
                .andExpect(content().string(containsString("href=\"/users?activeOnly=true\"")))
                .andExpect(content().string(containsString("<title>사용자 목록 - ch19</title>")));
    }

    @Test
    void detailShowsUser() throws Exception {
        mvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("users/detail"))
                .andExpect(content().string(containsString("kim@example.com")));
    }

    @Test
    void textEscapesHtmlButUtextDoesNot() throws Exception {
        mvc.perform(get("/users/4"))
                .andExpect(content().string(containsString("&lt;b&gt;choi&lt;/b&gt;")))
                .andExpect(content().string(containsString("<dd><b>choi</b></dd>")));
    }

    @Test
    void listEscapesHtmlInName() throws Exception {
        mvc.perform(get("/users"))
                .andExpect(content().string(containsString("&lt;b&gt;choi&lt;/b&gt;")))
                .andExpect(content().string(not(containsString(">" + "<b>choi</b>" + "</a>"))));
    }

    @Test
    void unknownUserIs404() throws Exception {
        mvc.perform(get("/users/999")).andExpect(status().isNotFound());
    }
}
