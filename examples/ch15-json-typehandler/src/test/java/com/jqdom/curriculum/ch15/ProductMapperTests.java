package com.jqdom.curriculum.ch15;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.transaction.annotation.Transactional;

/** 기본 Profile(H2)에서 항상 실행된다. */
@SpringBootTest
@Transactional
class ProductMapperTests {

    private final ProductAttributes laptop = new ProductAttributes(
            "acme", 1200, List.of("new", "sale"), new ProductAttributes.Specs("red", 1.5));

    @Autowired
    ProductMapper mapper;

    @Autowired
    JdbcClient jdbc;

    @Test
    void recordRoundTripsThroughJsonColumn() {
        Product product = new Product("laptop", laptop);

        mapper.insert(product);
        Product found = mapper.findById(product.getId());

        assertEquals(laptop, found.getAttributes());
    }

    @Test
    void columnHoldsPlainJsonText() {
        Product product = new Product("laptop", laptop);
        mapper.insert(product);

        String stored = jdbc.sql("SELECT attributes FROM product_catalog WHERE id = :id")
                .param("id", product.getId()).query(String.class).single();

        assertEquals("{\"brand\":\"acme\",\"price\":1200,\"tags\":[\"new\",\"sale\"],"
                + "\"specs\":{\"color\":\"red\",\"weightKg\":1.5}}", stored);
    }

    @Test
    void nullObjectIsStoredAsSqlNull() {
        Product product = new Product("empty", null);
        mapper.insert(product);

        assertNull(mapper.findById(product.getId()).getAttributes());
        assertEquals(1, jdbc.sql("SELECT COUNT(*) FROM product_catalog WHERE attributes IS NULL AND id = :id")
                .param("id", product.getId()).query(Integer.class).single());
    }

    @Test
    void jsonWrittenByOtherSystemsWithExtraKeysIsStillReadable() {
        jdbc.sql("INSERT INTO product_catalog (name, attributes) VALUES ('legacy', :json)")
                .param("json", "{\"brand\":\"old\",\"price\":10,\"tags\":[],\"specs\":null,\"discontinued\":true}")
                .update();

        Product legacy = mapper.findAll().stream().filter(p -> p.getName().equals("legacy")).findFirst().orElseThrow();

        assertEquals("old", legacy.getAttributes().brand());
        assertNull(legacy.getAttributes().specs());
    }
}
