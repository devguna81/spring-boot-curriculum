package com.devguna.curriculum.ch08;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.simple.JdbcClient;

@SpringBootTest
class UserRepositoryTests {

    @Autowired
    UserRepository repository;

    @Autowired
    JdbcClient jdbc;

    @Test
    void dataSqlLoadedInitialRows() {
        assertEquals(3, repository.count());
        assertEquals(List.of("kim", "lee", "park"),
                repository.findAll().stream().map(User::name).toList());
    }

    @Test
    void findByIdBindsNamedParameter() {
        User user = repository.findById(2).orElseThrow();

        assertEquals(new User(2, "lee", "lee@example.com", 35), user);
    }

    @Test
    void missingIdIsEmptyNotException() {
        assertTrue(repository.findById(999).isEmpty());
    }

    @Test
    void parameterBindingTreatsInputAsValueNotSql() {
        // 파라미터로 넘긴 문자열은 SQL 문장이 아니라 값으로만 쓰인다.
        int matched = jdbc.sql("SELECT COUNT(*) FROM users WHERE name = :name")
                .param("name", "x' OR '1'='1")
                .query(Integer.class)
                .single();

        assertEquals(0, matched);
    }
}
