package com.devguna.curriculum.ch13;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/** 지금 어떤 Profile로 어떤 데이터베이스에 붙었는지 보여 준다. 접속 URL의 계정 정보는 싣지 않는다. */
@RestController
public class DbInfoController {

    private final DataSource dataSource;
    private final JdbcClient jdbc;
    private final Environment environment;

    public DbInfoController(DataSource dataSource, JdbcClient jdbc, Environment environment) {
        this.dataSource = dataSource;
        this.jdbc = jdbc;
        this.environment = environment;
    }

    @GetMapping("/db/info")
    public Map<String, Object> info() throws SQLException {
        Map<String, Object> info = new LinkedHashMap<>();
        info.put("activeProfiles", environment.getActiveProfiles());
        try (Connection connection = dataSource.getConnection()) {
            info.put("product", connection.getMetaData().getDatabaseProductName());
            info.put("driver", connection.getMetaData().getDriverName());
        }
        // 어느 데이터베이스든 한 줄을 돌려주는 가장 단순한 연결 확인 질의.
        info.put("ping", jdbc.sql(pingSql()).query(Integer.class).single());
        return info;
    }

    private String pingSql() {
        return isOracle() ? "SELECT 1 FROM DUAL" : "SELECT 1";
    }

    private boolean isOracle() {
        try (Connection connection = dataSource.getConnection()) {
            return connection.getMetaData().getDatabaseProductName().toLowerCase().contains("oracle");
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }
}
