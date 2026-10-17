package com.jqdom.curriculum.ch15;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

import org.junit.jupiter.api.Test;
import tools.jackson.databind.json.JsonMapper;

/** Spring 없이 TypeHandler만 시험한다. 변환 규칙은 JDBC 객체를 가짜로 바꿔도 확인할 수 있다. */
class JsonTypeHandlerUnitTests {

    private final ProductAttributesTypeHandler handler = new ProductAttributesTypeHandler(JsonMapper.builder().build());

    private final ProductAttributes sample = new ProductAttributes(
            "acme", 1200, List.of("new", "sale"), new ProductAttributes.Specs("red", 1.5));

    private ProductAttributes read(String json) throws Exception {
        ResultSet rs = mock(ResultSet.class);
        when(rs.getString("attributes")).thenReturn(json);
        return handler.getNullableResult(rs, "attributes");
    }

    @Test
    void writesObjectAsJsonString() throws Exception {
        PreparedStatement ps = mock(PreparedStatement.class);

        handler.setNonNullParameter(ps, 1, sample, null);

        verify(ps).setString(1,
                "{\"brand\":\"acme\",\"price\":1200,\"tags\":[\"new\",\"sale\"],\"specs\":{\"color\":\"red\",\"weightKg\":1.5}}");
    }

    @Test
    void readsJsonStringIntoObject() throws Exception {
        String json = "{\"brand\":\"acme\",\"price\":1200,\"tags\":[\"new\",\"sale\"],"
                + "\"specs\":{\"color\":\"red\",\"weightKg\":1.5}}";

        assertEquals(sample, read(json));
    }

    @Test
    void nullAndBlankColumnBecomeNull() throws Exception {
        assertNull(read(null));
        assertNull(read("  "));
    }

    @Test
    void keyOrderAndWhitespaceDoNotMatter() throws Exception {
        String json = "{ \"specs\": {\"weightKg\": 1.5, \"color\": \"red\"}, \"tags\": [\"new\",\"sale\"],"
                + " \"price\": 1200, \"brand\": \"acme\" }";

        assertEquals(sample, read(json));
    }

    @Test
    void missingObjectKeysBecomeNull() throws Exception {
        ProductAttributes partial = read("{\"brand\":\"acme\",\"price\":10}");

        assertEquals("acme", partial.brand());
        assertEquals(10, partial.price());
        assertNull(partial.tags());
        assertNull(partial.specs());
    }

    @Test
    void missingPrimitiveKeyIsRejectedInsteadOfBecomingZero() {
        // price가 int(기본형)이면 키가 없을 때 0이 되지 않고 읽기에 실패한다. 없을 수 있는 값은 Integer로 선언한다.
        IllegalStateException e = assertThrows(IllegalStateException.class, () -> read("{\"brand\":\"acme\"}"));

        assertEquals(true, e.getCause().getMessage().contains("Cannot map `null` into type `int`"));
    }

    @Test
    void invalidJsonFailsWithTheOffendingValueInTheMessage() {
        IllegalStateException e = assertThrows(IllegalStateException.class, () -> read("{not json"));

        assertEquals(true, e.getMessage().contains("{not json"));
    }
}
