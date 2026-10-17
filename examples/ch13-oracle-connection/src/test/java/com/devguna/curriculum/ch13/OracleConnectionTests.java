package com.devguna.curriculum.ch13;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Connection;
import java.util.List;

import javax.sql.DataSource;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.test.context.ActiveProfiles;

/**
 * Oracle이 있을 때만 실행한다. ORACLE_URL 환경 변수가 없으면 건너뛴다.
 * 로컬 실행 방법은 compose.yml과 README의 안내를 따른다.
 */
@Tag("oracle")
@EnabledIfEnvironmentVariable(named = "ORACLE_URL", matches = ".+")
@SpringBootTest
@ActiveProfiles("oracle")
class OracleConnectionTests {

    @Autowired
    DataSource dataSource;

    @Autowired
    JdbcClient jdbc;

    @Autowired
    UserMapper mapper;

    @BeforeEach
    void recreateTable() throws Exception {
        // 23ai의 DROP TABLE IF EXISTS를 쓴다. 이전 버전 Oracle이라면 예외를 무시하는 방식으로 바꾼다.
        jdbc.sql("DROP TABLE IF EXISTS users").update();
        try (Connection connection = dataSource.getConnection()) {
            ScriptUtils.executeSqlScript(connection, new ClassPathResource("oracle/schema-oracle.sql"));
        }
    }

    @Test
    void connectsToOracle() throws Exception {
        try (Connection connection = dataSource.getConnection()) {
            assertTrue(connection.getMetaData().getDatabaseProductName().contains("Oracle"));
            assertTrue(connection.isValid(2));
        }
        assertEquals(1, jdbc.sql("SELECT 1 FROM DUAL").query(Integer.class).single());
    }

    @Test
    void insertFillsGeneratedIdFromIdentityColumn() {
        User user = new User("kim", "kim@example.com", "kimmy", 28);

        mapper.insert(user);

        assertNotNull(user.getId());
        User found = mapper.findById(user.getId());
        assertEquals("kim", found.getName());
        assertNotNull(found.getCreatedAt());
    }

    @Test
    void nullNicknameWorksWithJdbcTypeForNull() {
        User user = new User("lee", "lee@example.com", null, 35);

        mapper.insert(user);

        assertNull(mapper.findById(user.getId()).getNickname());
    }

    @Test
    void emptyStringIsStoredAsNull() {
        User user = new User("park", "park@example.com", "", 42);

        mapper.insert(user);

        // Oracle은 빈 문자열을 NULL로 저장한다. H2나 다른 데이터베이스와 다른 점이다.
        assertNull(mapper.findById(user.getId()).getNickname());
    }

    @Test
    void uniqueViolationIsTranslatedToSpringException() {
        mapper.insert(new User("kim", "dup@example.com", null, 28));

        assertThrows(DuplicateKeyException.class,
                () -> mapper.insert(new User("other", "dup@example.com", null, 20)));
    }

    @Test
    void offsetFetchPagingWorks() {
        for (int i = 1; i <= 7; i++) {
            mapper.insert(new User("u" + i, "u" + i + "@example.com", null, 20 + i));
        }

        assertEquals(7, mapper.count());
        assertEquals(List.of("u4", "u5", "u6"), mapper.findPage(3, 3).stream().map(User::getName).toList());
    }
}
