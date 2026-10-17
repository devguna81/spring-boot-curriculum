package com.devguna.curriculum.ch15;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.test.context.ActiveProfiles;

/** Oracle 21c 이상의 JSON 타입 컬럼에서 같은 TypeHandler가 동작하는지 확인한다. ORACLE_URL이 없으면 건너뛴다. */
@Tag("oracle")
@EnabledIfEnvironmentVariable(named = "ORACLE_URL", matches = ".+")
@SpringBootTest
@ActiveProfiles("oracle")
class OracleJsonColumnTests {

    private final ProductAttributes laptop = new ProductAttributes(
            "acme", 1200, List.of("new", "sale"), new ProductAttributes.Specs("red", 1.5));

    @Autowired
    DataSource dataSource;

    @Autowired
    JdbcClient jdbc;

    @Autowired
    ProductMapper mapper;

    @BeforeEach
    void recreateTable() throws Exception {
        jdbc.sql("DROP TABLE IF EXISTS product_catalog").update();
        try (Connection connection = dataSource.getConnection()) {
            ScriptUtils.executeSqlScript(connection, new ClassPathResource("oracle/schema-oracle.sql"));
        }
    }

    @Test
    void recordRoundTripsThroughOracleJsonColumn() {
        Product product = new Product("laptop", laptop);

        mapper.insert(product);

        assertEquals(laptop, mapper.findById(product.getId()).getAttributes());
    }

    @Test
    void valueWrittenFromJavaIsQueryableWithJsonFunctions() {
        Product product = new Product("laptop", laptop);
        mapper.insert(product);

        String brand = jdbc.sql("SELECT JSON_VALUE(attributes, '$.brand') FROM product_catalog WHERE id = :id")
                .param("id", product.getId()).query(String.class).single();
        Integer price = jdbc.sql("SELECT JSON_VALUE(attributes, '$.price' RETURNING NUMBER) FROM product_catalog WHERE id = :id")
                .param("id", product.getId()).query(Integer.class).single();

        assertEquals("acme", brand);
        assertEquals(1200, price);
    }

    @Test
    void nullObjectIsStoredAsSqlNull() {
        Product product = new Product("empty", null);

        mapper.insert(product);

        assertEquals(null, mapper.findById(product.getId()).getAttributes());
    }
}
