package com.devguna.curriculum.finalproject.post;

import java.sql.Connection;

import javax.sql.DataSource;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.test.context.ActiveProfiles;

/** 같은 시나리오를 Oracle(JSON 컬럼, JSON_EXISTS)에서 돌린다. ORACLE_URL이 없으면 건너뛴다. */
@Tag("oracle")
@EnabledIfEnvironmentVariable(named = "ORACLE_URL", matches = ".+")
@ActiveProfiles("oracle")
class PostSearchOracleTests extends PostSearchScenarios {

    @Autowired
    DataSource dataSource;

    @Autowired
    JdbcClient jdbc;

    @Override
    void prepareSchema() throws Exception {
        jdbc.sql("DROP TABLE IF EXISTS fp_posts").update();
        jdbc.sql("DROP TABLE IF EXISTS fp_users").update();
        try (Connection connection = dataSource.getConnection()) {
            ScriptUtils.executeSqlScript(connection, new ClassPathResource("oracle/schema-oracle.sql"));
        }
    }
}
