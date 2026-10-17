package com.devguna.curriculum.ch18;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import tools.jackson.databind.JsonNode;

/**
 * 이전 OpenAPI 문서와 새 문서를 비교해 클라이언트를 깨뜨리는 변경을 찾는다.
 * 모든 경우를 다루지는 않고, 자주 일어나는 다음 다섯 가지만 본다.
 * <ul>
 *   <li>경로 또는 HTTP 메서드가 사라짐</li>
 *   <li>응답 스키마의 속성이 사라짐</li>
 *   <li>속성의 타입이 바뀜</li>
 *   <li>요청 스키마에 필수 속성이 새로 생김</li>
 *   <li>응답 코드가 사라짐</li>
 * </ul>
 * 속성 추가(선택), 새 경로 추가, 설명 변경은 호환되는 변경으로 본다.
 */
final class ContractDiff {

    private ContractDiff() {
    }

    static List<String> breakingChanges(JsonNode before, JsonNode after) {
        List<String> breaking = new ArrayList<>();
        comparePaths(before.path("paths"), after.path("paths"), breaking);
        compareSchemas(before.path("components").path("schemas"), after.path("components").path("schemas"), breaking);
        return breaking;
    }

    private static void comparePaths(JsonNode before, JsonNode after, List<String> breaking) {
        for (Map.Entry<String, JsonNode> path : before.properties()) {
            JsonNode newPath = after.path(path.getKey());
            for (Map.Entry<String, JsonNode> method : path.getValue().properties()) {
                JsonNode newMethod = newPath.path(method.getKey());
                if (newMethod.isMissingNode()) {
                    breaking.add("삭제된 엔드포인트: " + method.getKey().toUpperCase() + " " + path.getKey());
                    continue;
                }
                for (Map.Entry<String, JsonNode> response : method.getValue().path("responses").properties()) {
                    if (newMethod.path("responses").path(response.getKey()).isMissingNode()) {
                        breaking.add("삭제된 응답 코드: " + method.getKey().toUpperCase() + " " + path.getKey()
                                + " " + response.getKey());
                    }
                }
            }
        }
    }

    private static void compareSchemas(JsonNode before, JsonNode after, List<String> breaking) {
        for (Map.Entry<String, JsonNode> schema : before.properties()) {
            JsonNode newSchema = after.path(schema.getKey());
            if (newSchema.isMissingNode()) {
                breaking.add("삭제된 스키마: " + schema.getKey());
                continue;
            }
            for (Map.Entry<String, JsonNode> property : schema.getValue().path("properties").properties()) {
                JsonNode newProperty = newSchema.path("properties").path(property.getKey());
                if (newProperty.isMissingNode()) {
                    breaking.add("삭제된 속성: " + schema.getKey() + "." + property.getKey());
                } else if (!property.getValue().path("type").equals(newProperty.path("type"))
                        || !property.getValue().path("$ref").equals(newProperty.path("$ref"))) {
                    breaking.add("속성 타입 변경: " + schema.getKey() + "." + property.getKey());
                }
            }
            Set<String> oldRequired = names(schema.getValue().path("required"));
            for (String required : names(newSchema.path("required"))) {
                if (!oldRequired.contains(required)) {
                    breaking.add("새 필수 속성: " + schema.getKey() + "." + required);
                }
            }
        }
    }

    private static Set<String> names(JsonNode array) {
        Set<String> names = new HashSet<>();
        for (JsonNode element : array) {
            names.add(element.asString());
        }
        return names;
    }
}
