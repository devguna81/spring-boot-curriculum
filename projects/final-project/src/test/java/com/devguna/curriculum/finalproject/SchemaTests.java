package com.devguna.curriculum.finalproject;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.transaction.annotation.Transactional;

/** f01에서 설계한 테이블이 H2에서 실제로 만들어지고 제약이 동작하는지 확인한다. 각 테스트는 롤백된다. */
@SpringBootTest
@Transactional
class SchemaTests {

    @Autowired
    JdbcClient jdbc;

    private long insertUser(String username, String role) {
        jdbc.sql("INSERT INTO fp_users (username, password_hash, display_name, role) VALUES (:u, 'hash', :u, :r)")
                .param("u", username).param("r", role).update();
        return jdbc.sql("SELECT id FROM fp_users WHERE username = :u").param("u", username).query(Long.class).single();
    }

    private void insertPost(long authorId, String meta) {
        jdbc.sql("INSERT INTO fp_posts (author_id, title, content, meta) VALUES (:a, 't', 'c', :m)")
                .param("a", authorId).param("m", meta).update();
    }

    @Test
    void usernameMustBeUnique() {
        insertUser("kim", "USER");

        assertThatThrownBy(() -> insertUser("kim", "USER")).isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void roleIsLimitedToUserAndAdmin() {
        assertThatThrownBy(() -> insertUser("x", "SUPER")).isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void postNeedsAnExistingAuthor() {
        assertThatThrownBy(() -> insertPost(9999, "{}")).isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void metaIsStoredAsTheJsonTextItWasGiven() {
        long author = insertUser("lee", "USER");
        String meta = "{\"tags\":[\"spring\",\"java\"]}";
        insertPost(author, meta);

        String stored = jdbc.sql("SELECT meta FROM fp_posts WHERE author_id = :a").param("a", author)
                .query(String.class).single();

        assertThat(stored).isEqualTo(meta);
    }

    @Test
    void createdAtIsFilledByTheDatabase() {
        long author = insertUser("park", "ADMIN");
        insertPost(author, "{}");

        assertThat(jdbc.sql("SELECT created_at FROM fp_posts WHERE author_id = :a").param("a", author)
                .query(java.sql.Timestamp.class).single()).isNotNull();
    }
}
