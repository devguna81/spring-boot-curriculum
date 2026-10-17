package com.devguna.curriculum.ch18;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

class ContractDiffTests {

    private final JsonMapper mapper = JsonMapper.builder().build();

    private static final String BASE = """
            {"paths":{"/api/v1/users":{"get":{"responses":{"200":{}}},"post":{"responses":{"201":{},"400":{}}}}},
             "components":{"schemas":{
               "UserView":{"type":"object","properties":{"id":{"type":"integer"},"email":{"type":"string"}}},
               "CreateUserRequest":{"type":"object","required":["name"],"properties":{"name":{"type":"string"},"age":{"type":"integer"}}}}}}""";

    private List<String> diff(String after) throws Exception {
        JsonNode before = mapper.readTree(BASE);
        return ContractDiff.breakingChanges(before, mapper.readTree(after));
    }

    private String replace(String from, String to) {
        assertTrue(BASE.contains(from), from);
        return BASE.replace(from, to);
    }

    @Test
    void identicalDocumentsHaveNoBreakingChange() throws Exception {
        assertEquals(List.of(), diff(BASE));
    }

    @Test
    void addingAnOptionalResponsePropertyIsCompatible() throws Exception {
        String after = replace("\"email\":{\"type\":\"string\"}}}", "\"email\":{\"type\":\"string\"},\"nickname\":{\"type\":\"string\"}}}");

        assertEquals(List.of(), diff(after));
    }

    @Test
    void addingANewEndpointIsCompatible() throws Exception {
        String after = replace("\"paths\":{", "\"paths\":{\"/api/v1/health\":{\"get\":{\"responses\":{\"200\":{}}}},");

        assertEquals(List.of(), diff(after));
    }

    @Test
    void removingAResponsePropertyIsBreaking() throws Exception {
        String after = replace(",\"email\":{\"type\":\"string\"}", "");

        assertEquals(List.of("삭제된 속성: UserView.email"), diff(after));
    }

    @Test
    void changingAPropertyTypeIsBreaking() throws Exception {
        String after = replace("\"id\":{\"type\":\"integer\"}", "\"id\":{\"type\":\"string\"}");

        assertEquals(List.of("속성 타입 변경: UserView.id"), diff(after));
    }

    @Test
    void makingARequestPropertyRequiredIsBreaking() throws Exception {
        String after = replace("\"required\":[\"name\"]", "\"required\":[\"name\",\"age\"]");

        assertEquals(List.of("새 필수 속성: CreateUserRequest.age"), diff(after));
    }

    @Test
    void removingAnEndpointMethodIsBreaking() throws Exception {
        String after = replace("\"get\":{\"responses\":{\"200\":{}}},", "");

        assertEquals(List.of("삭제된 엔드포인트: GET /api/v1/users"), diff(after));
    }

    @Test
    void removingAResponseCodeIsBreaking() throws Exception {
        String after = replace(",\"400\":{}", "");

        assertEquals(List.of("삭제된 응답 코드: POST /api/v1/users 400"), diff(after));
    }
}
