package com.devguna.curriculum.finalproject.web;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.List;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.devguna.curriculum.finalproject.common.Actor;
import com.devguna.curriculum.finalproject.contract.PostRequest;
import com.devguna.curriculum.finalproject.contract.PostView;
import com.devguna.curriculum.finalproject.contract.Role;
import com.devguna.curriculum.finalproject.contract.UserView;
import com.devguna.curriculum.finalproject.post.api.PostApi;
import com.devguna.curriculum.finalproject.user.api.UserApi;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class PageControllerTests {

    @Autowired
    MockMvc mvc;

    @Autowired
    UserApi userApi;

    @Autowired
    PostApi postApi;

    Actor actor;

    @BeforeEach
    void user() {
        UserView user = userApi.createAccount("writer", "password-1234", "작성자", Role.USER);
        actor = new Actor(user.id(), user.role());
    }

    @Test
    void listShowsPostsAndSearchForm() throws Exception {
        postApi.create(actor, new PostRequest("첫 글", "본문", List.of("spring"), false));

        mvc.perform(get("/pages/posts"))
                .andExpect(status().isOk())
                .andExpect(view().name("posts/list"))
                .andExpect(content().string(Matchers.containsString("첫 글")))
                .andExpect(content().string(Matchers.containsString("전체 1건")))
                .andExpect(content().string(Matchers.containsString("name=\"keyword\"")));
    }

    @Test
    void listFiltersByKeywordAndKeepsItInTheForm() throws Exception {
        postApi.create(actor, new PostRequest("스프링", "본문", List.of(), false));
        postApi.create(actor, new PostRequest("오라클", "본문", List.of(), false));

        mvc.perform(get("/pages/posts").param("keyword", "스프링"))
                .andExpect(content().string(Matchers.containsString("전체 1건")))
                .andExpect(content().string(Matchers.not(Matchers.containsString("오라클"))))
                .andExpect(content().string(Matchers.containsString("value=\"스프링\"")));
    }

    @Test
    void emptyResultShowsMessage() throws Exception {
        mvc.perform(get("/pages/posts").param("keyword", "없는검색어"))
                .andExpect(content().string(Matchers.containsString("조건에 맞는 글이 없습니다")));
    }

    @Test
    void htmlInTitleAndContentIsEscaped() throws Exception {
        PostView post = postApi.create(actor,
                new PostRequest("<script>alert(1)</script>", "<img src=x onerror=alert(2)>", List.of(), false));

        String list = mvc.perform(get("/pages/posts")).andReturn().getResponse().getContentAsString();
        String detail = mvc.perform(get("/pages/posts/" + post.id())).andReturn().getResponse().getContentAsString();

        org.assertj.core.api.Assertions.assertThat(list).doesNotContain("<script>alert(1)</script>")
                .contains("&lt;script&gt;");
        org.assertj.core.api.Assertions.assertThat(detail).doesNotContain("<img src=x")
                .contains("&lt;img src=x");
    }

    @Test
    void detailShowsPost() throws Exception {
        PostView post = postApi.create(actor, new PostRequest("상세 제목", "여러 줄\n본문", List.of("t1"), true));

        mvc.perform(get("/pages/posts/" + post.id()))
                .andExpect(status().isOk())
                .andExpect(view().name("posts/detail"))
                .andExpect(content().string(Matchers.containsString("상세 제목")))
                .andExpect(content().string(Matchers.containsString("작성자")))
                .andExpect(content().string(Matchers.containsString("t1")));
    }

    @Test
    void unknownPostShowsNotFoundPage() throws Exception {
        mvc.perform(get("/pages/posts/999999"))
                .andExpect(status().isNotFound())
                .andExpect(view().name("error/404"));
    }

    @Test
    void invalidPageNumberShowsBadRequestPage() throws Exception {
        mvc.perform(get("/pages/posts").param("page", "-1"))
                .andExpect(status().isBadRequest())
                .andExpect(view().name("error/400"));
    }

    @Test
    void rootRedirectsToApp() throws Exception {
        mvc.perform(get("/")).andExpect(status().is3xxRedirection()).andExpect(redirectedUrl("/app/index.html"));
    }
}
