package com.jqdom.curriculum.finalproject;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.sql.Connection;

import javax.sql.DataSource;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.test.context.ActiveProfiles;

/** Oracle 스키마(JSON 컬럼 포함)를 실제 Oracle에서 만들어 본다. ORACLE_URL이 없으면 건너뛴다. */
@Tag("oracle")
@EnabledIfEnvironmentVariable(named = "ORACLE_URL", matches = ".+")
@SpringBootTest
@ActiveProfiles("oracle")
class OracleSchemaTests {

    @Autowired
    DataSource dataSource;

    @Autowired
    JdbcClient jdbc;

    @BeforeEach
    void recreateTables() throws Exception {
        jdbc.sql("DROP TABLE IF EXISTS fp_posts").update();
        jdbc.sql("DROP TABLE IF EXISTS fp_users").update();
        try (Connection connection = dataSource.getConnection()) {
            ScriptUtils.executeSqlScript(connection, new ClassPathResource("oracle/schema-oracle.sql"));
        }
    }

    private long insertUser(String username, String role) {
        jdbc.sql("INSERT INTO fp_users (username, password_hash, display_name, role) VALUES (:u, 'hash', :u, :r)")
                .param("u", username).param("r", role).update();
        return jdbc.sql("SELECT id FROM fp_users WHERE username = :u").param("u", username).query(Long.class).single();
    }

    @Test
    void jsonMetaIsQueryableWithJsonFunctions() {
        long author = insertUser("kim", "USER");
        jdbc.sql("INSERT INTO fp_posts (author_id, title, content, meta) VALUES (:a, 't', 'c', :m)")
                .param("a", author).param("m", "{\"tags\":[\"spring\",\"java\"]}").update();

        Long matched = jdbc.sql("SELECT COUNT(*) FROM fp_posts WHERE JSON_EXISTS(meta, '$.tags[*]?(@ == \"spring\")')")
                .query(Long.class).single();

        assertThat(matched).isEqualTo(1);
    }

    @Test
    void constraintsMatchTheH2Schema() {
        insertUser("kim", "USER");

        assertThatThrownBy(() -> insertUser("kim", "USER")).isInstanceOf(DataIntegrityViolationException.class);
        assertThatThrownBy(() -> insertUser("x", "SUPER")).isInstanceOf(DataIntegrityViolationException.class);
    }
}
