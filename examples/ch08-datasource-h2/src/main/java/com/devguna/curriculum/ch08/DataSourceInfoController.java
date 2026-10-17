package com.devguna.curriculum.ch08;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.sql.DataSource;

import com.zaxxer.hikari.HikariDataSource;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/** 자동 구성된 DataSource와 커넥션 풀의 설정을 그대로 보여 준다. */
@RestController
public class DataSourceInfoController {

    private final DataSource dataSource;

    public DataSourceInfoController(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @GetMapping("/db/info")
    public Map<String, Object> info() throws SQLException {
        Map<String, Object> info = new LinkedHashMap<>();
        info.put("dataSourceType", dataSource.getClass().getSimpleName());
        if (dataSource instanceof HikariDataSource hikari) {
            info.put("poolName", hikari.getPoolName());
            info.put("maximumPoolSize", hikari.getMaximumPoolSize());
            info.put("minimumIdle", hikari.getMinimumIdle());
        }
        try (Connection connection = dataSource.getConnection()) {
            info.put("product", connection.getMetaData().getDatabaseProductName());
            info.put("url", connection.getMetaData().getURL());
        }
        return info;
    }
}
