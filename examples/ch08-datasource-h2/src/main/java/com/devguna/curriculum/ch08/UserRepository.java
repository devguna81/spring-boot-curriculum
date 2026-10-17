package com.devguna.curriculum.ch08;

import java.util.List;
import java.util.Optional;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

/**
 * JdbcClient로 SQL을 직접 실행하는 저장소.
 * 컬럼 값을 record 생성자 인자에 이름으로 연결해 주므로 RowMapper를 따로 쓰지 않는다.
 */
@Repository
public class UserRepository {

    private final JdbcClient jdbc;

    public UserRepository(JdbcClient jdbc) {
        this.jdbc = jdbc;
    }

    public List<User> findAll() {
        return jdbc.sql("SELECT id, name, email, age FROM users ORDER BY id")
                .query(User.class)
                .list();
    }

    public Optional<User> findById(long id) {
        return jdbc.sql("SELECT id, name, email, age FROM users WHERE id = :id")
                .param("id", id)
                .query(User.class)
                .optional();
    }

    public int count() {
        return jdbc.sql("SELECT COUNT(*) FROM users")
                .query(Integer.class)
                .single();
    }
}
