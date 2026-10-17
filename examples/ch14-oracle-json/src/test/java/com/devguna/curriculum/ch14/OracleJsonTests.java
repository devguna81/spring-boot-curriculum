package com.devguna.curriculum.ch14;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.test.context.ActiveProfiles;

/** Oracle 21c 이상이 있을 때만 실행한다(JSON 타입 컬럼 사용). ORACLE_URL이 없으면 건너뛴다. */
@Tag("oracle")
@EnabledIfEnvironmentVariable(named = "ORACLE_URL", matches = ".+")
@SpringBootTest
@ActiveProfiles("oracle")
class OracleJsonTests {

    private static final String LAPTOP = """
            {"brand":"acme","price":1200,"tags":["new","sale"],"specs":{"color":"red","weightKg":1.5}}""";
    private static final String PHONE = """
            {"brand":"zeta","price":800,"tags":["new"],"specs":{"color":"black","weightKg":0.2}}""";
    private static final String MOUSE = """
            {"brand":"acme","price":30,"tags":[],"specs":{"color":"white","weightKg":0.1}}""";

    @Autowired
    DataSource dataSource;

    @Autowired
    JdbcClient jdbc;

    @Autowired
    ProductMapper mapper;

    long laptopId;
    long phoneId;
    long mouseId;

    @BeforeEach
    void recreateTables() throws Exception {
        jdbc.sql("DROP TABLE IF EXISTS products").update();
        jdbc.sql("DROP TABLE IF EXISTS products_clob").update();
        try (Connection connection = dataSource.getConnection()) {
            ScriptUtils.executeSqlScript(connection, new ClassPathResource("oracle/schema-oracle.sql"));
        }
        laptopId = insert("laptop", LAPTOP);
        phoneId = insert("phone", PHONE);
        mouseId = insert("mouse", MOUSE);
    }

    private long insert(String name, String json) {
        Product product = new Product(name, json);
        mapper.insert(product);
        return product.getId();
    }

    @Test
    void jsonValueExtractsScalar() {
        assertEquals("acme", mapper.findBrand(laptopId));
        assertEquals("zeta", mapper.findBrand(phoneId));
    }

    @Test
    void jsonValueOnMissingPathIsNull() {
        jdbc.sql("UPDATE products SET attributes = '{\"price\":1}' WHERE id = :id").param("id", mouseId).update();

        assertNull(mapper.findBrand(mouseId));
    }

    @Test
    void jsonQueryReturnsObjectAsJsonText() {
        String specs = mapper.findSpecs(laptopId);

        assertTrue(specs.contains("\"color\":\"red\""), specs);
        assertTrue(specs.contains("\"weightKg\":1.5"), specs);
    }

    @Test
    void jsonValueReturningNumberComparesNumerically() {
        List<String> names = mapper.findPriceOver(500).stream().map(Product::getName).toList();

        assertEquals(List.of("laptop", "phone"), names);
    }

    @Test
    void withoutReturningNumberTheComparisonIsAlphabetical() {
        List<String> names = mapper.findPriceOverComparedAsText("500").stream().map(Product::getName).toList();

        // "1200"은 사전순으로 "500"보다 앞이라 laptop이 빠진다. 숫자 비교가 필요하면 RETURNING NUMBER를 쓴다.
        assertEquals(List.of("phone"), names);
    }

    @Test
    void jsonExistsFiltersByArrayElement() {
        assertEquals(List.of("laptop", "phone"), mapper.findByTag("new").stream().map(Product::getName).toList());
        assertEquals(List.of("laptop"), mapper.findByTag("sale").stream().map(Product::getName).toList());
        assertEquals(List.of(), mapper.findByTag("none"));
    }

    @Test
    void jsonTableExpandsArrayIntoRows() {
        assertEquals(List.of("new", "sale"), mapper.findTags(laptopId));
        assertEquals(List.of(), mapper.findTags(mouseId));
    }

    @Test
    void jsonTypeColumnRejectsInvalidJson() {
        Product broken = new Product("broken", "{not json");

        assertThrows(DataAccessException.class, () -> mapper.insert(broken));
        assertEquals(3, mapper.count());
    }

    @Test
    void clobColumnWithIsJsonCheckAlsoRejectsInvalidJson() {
        mapper.insertClob(new Product("ok", LAPTOP));

        assertThrows(DataAccessException.class, () -> mapper.insertClob(new Product("broken", "{not json")));
    }

    @Test
    void clobKeepsTextExactlyAsInsertedWhileJsonTypeNormalizesIt() {
        String spaced = "{ \"brand\" :  \"acme\" }";
        Product clob = new Product("clob", spaced);
        mapper.insertClob(clob);
        long jsonId = insert("jsontype", spaced);

        assertEquals(spaced, mapper.findClobById(clob.getId()).getAttributes());
        assertEquals("{\"brand\":\"acme\"}", mapper.findById(jsonId).getAttributes());
    }
}
