package com.devguna.curriculum.finalproject.post;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.transaction.annotation.Transactional;

import com.devguna.curriculum.finalproject.common.Actor;
import com.devguna.curriculum.finalproject.contract.PostRequest;
import com.devguna.curriculum.finalproject.contract.PostView;
import com.devguna.curriculum.finalproject.contract.Role;
import com.devguna.curriculum.finalproject.contract.UserView;
import com.devguna.curriculum.finalproject.post.api.PostApi;
import com.devguna.curriculum.finalproject.user.api.UserApi;

@SpringBootTest
@Transactional
class PostMetaTypeHandlerTests {

    @Autowired
    UserApi userApi;

    @Autowired
    PostApi postApi;

    @Autowired
    JdbcClient jdbc;

    private Actor actor() {
        UserView user = userApi.createAccount("writer", "password-1234", "작성자", Role.USER);
        return new Actor(user.id(), user.role());
    }

    @Test
    void tagsAreStoredAsJsonAndReadBack() {
        PostView created = postApi.create(actor(), new PostRequest("제목", "본문", List.of("a", "b"), false));

        String stored = jdbc.sql("SELECT meta FROM fp_posts WHERE id = :id").param("id", created.id())
                .query(String.class).single();

        assertThat(stored).isEqualTo("{\"tags\":[\"a\",\"b\"]}");
        assertThat(postApi.get(created.id()).tags()).containsExactly("a", "b");
    }

    @Test
    void missingTagsBecomeEmptyList() {
        PostView created = postApi.create(actor(), new PostRequest("제목", "본문", null, null));

        assertThat(postApi.get(created.id()).tags()).isEmpty();
        assertThat(created.pinned()).isFalse();
    }

    @Test
    void updateReplacesTags() {
        Actor actor = actor();
        PostView created = postApi.create(actor, new PostRequest("제목", "본문", List.of("old"), false));

        PostView updated = postApi.update(actor, created.id(), new PostRequest("새 제목", "새 본문", List.of("new"), true));

        assertThat(updated.tags()).containsExactly("new");
        assertThat(updated.title()).isEqualTo("새 제목");
        assertThat(updated.pinned()).isTrue();
    }
}
