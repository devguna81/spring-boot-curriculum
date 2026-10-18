package com.jqdom.curriculum.ch08;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import com.zaxxer.hikari.HikariDataSource;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class DataSourceTests {

    @Autowired
    DataSource dataSource;

    @Test
    void bootCreatesHikariPoolFromProperties() {
        HikariDataSource hikari = assertInstanceOf(HikariDataSource.class, dataSource);

        assertEquals("curriculum-pool", hikari.getPoolName());
        assertEquals(5, hikari.getMaximumPoolSize());
        assertEquals(2, hikari.getMinimumIdle());
    }

    @Test
    void connectionPointsToInMemoryH2() throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            assertEquals("H2", connection.getMetaData().getDatabaseProductName());
            assertTrue(connection.getMetaData().getURL().startsWith("jdbc:h2:mem:curriculum-"));
        }
    }

    @Test
    void closingConnectionReturnsItToPoolInsteadOfClosingPhysically() throws SQLException {
        Connection first = dataSource.getConnection();
        first.close();
        Connection second = dataSource.getConnection();
        try {
            // 풀이 돌려주는 것은 매번 새 프록시 객체지만, 닫은 뒤에도 새 연결을 얻을 수 있다.
            assertNotSame(first, second);
            assertTrue(second.isValid(1));
            assertTrue(first.isClosed());
        } finally {
            second.close();
        }
    }
}
