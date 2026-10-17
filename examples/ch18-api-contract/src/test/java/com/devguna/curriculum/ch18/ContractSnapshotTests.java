package com.devguna.curriculum.ch18;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.node.ArrayNode;
import tools.jackson.databind.node.ObjectNode;

/**
 * 지금 코드가 만드는 OpenAPI 문서를 저장해 둔 계약(src/test/resources/contract/openapi-v1.json)과 비교한다.
 * 계약이 바뀌면 이 테스트가 실패한다. 의도한 변경이면 -Dcontract.update=true로 저장본을 갱신하고,
 * 갱신 전에 ContractDiff로 호환되는 변경인지 확인한다.
 */
@SpringBootTest
@AutoConfigureMockMvc
class ContractSnapshotTests {

    private static final Path SNAPSHOT = Path.of("src/test/resources/contract/openapi-v1.json");

    private final JsonMapper mapper = JsonMapper.builder().build();

    @Autowired
    MockMvc mockMvc;

    /** required 배열의 순서는 실행마다 다를 수 있어 정렬한 뒤 비교한다. */
    private JsonNode normalize(JsonNode node) {
        if (node instanceof ObjectNode object) {
            for (Map.Entry<String, JsonNode> entry : object.properties()) {
                if (entry.getKey().equals("required") && entry.getValue() instanceof ArrayNode array) {
                    List<String> sorted = new java.util.ArrayList<>();
                    array.forEach(e -> sorted.add(e.asString()));
                    java.util.Collections.sort(sorted);
                    ArrayNode replaced = mapper.createArrayNode();
                    sorted.forEach(replaced::add);
                    object.set("required", replaced);
                } else {
                    normalize(entry.getValue());
                }
            }
        } else if (node instanceof ArrayNode array) {
            array.forEach(this::normalize);
        }
        return node;
    }

    private JsonNode current() throws Exception {
        String json = mockMvc.perform(get("/v3/api-docs")).andReturn().getResponse().getContentAsString();
        return normalize(mapper.readTree(json));
    }

    @Test
    void currentDocumentMatchesSavedContract() throws Exception {
        JsonNode current = current();
        if (Boolean.getBoolean("contract.update")) {
            Files.createDirectories(SNAPSHOT.getParent());
            Files.writeString(SNAPSHOT, mapper.writerWithDefaultPrettyPrinter().writeValueAsString(current) + "\n");
        }
        JsonNode saved = normalize(mapper.readTree(Files.readString(SNAPSHOT)));

        assertEquals(saved, current, () -> "API 계약이 저장본과 다릅니다. 호환성 확인 결과: "
                + ContractDiff.breakingChanges(saved, current)
                + "\n의도한 변경이면 -Dcontract.update=true 로 저장본을 갱신하세요.");
    }

    @Test
    void currentChangesAreBackwardCompatibleWithSavedContract() throws Exception {
        JsonNode saved = normalize(mapper.readTree(Files.readString(SNAPSHOT)));

        assertEquals(List.of(), ContractDiff.breakingChanges(saved, current()));
    }

    @Test
    void savedContractDoesNotMentionInternalFields() throws Exception {
        String text = Files.readString(SNAPSHOT);

        assertEquals(false, text.contains("internalMemo"));
        assertEquals(false, text.contains("suspended"));
    }
}
