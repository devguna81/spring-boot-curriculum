package com.devguna.curriculum.ch29;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;

/**
 * 전 계층 통합 시나리오. 진짜 서버를 임의 포트에 띄우고 진짜 HTTP로 호출한다.
 * 컨트롤러, 검증, 서비스, MyBatis, H2가 모두 실제로 이어진다.
 * 단계들이 서로의 결과(생성된 id)에 기대므로 순서를 고정한다.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserScenarioIntegrationTests {

    @LocalServerPort
    int port;

    private static final ParameterizedTypeReference<List<Map<String, Object>>> LIST = new ParameterizedTypeReference<>() { };

    private static Long createdId;

    private RestClient client() {
        // 4xx/5xx에서 예외를 던지지 않고 상태 코드로 검사하기 위해 기본 오류 핸들러를 끈다.
        return RestClient.builder().baseUrl("http://localhost:" + port)
                .defaultStatusHandler(HttpStatusCode::isError, (req, res) -> { })
                .build();
    }

    private static String body(String name, String email, int age) {
        return "{\"name\":\"%s\",\"email\":\"%s\",\"age\":%d}".formatted(name, email, age);
    }

    @Test
    @Order(1)
    void initialDataIsVisible() {
        List<Map<String, Object>> users = client().get().uri("/users").retrieve().body(LIST);

        assertThat(users).extracting(u -> u.get("name")).contains("kim", "lee", "park");
    }

    @Test
    @Order(2)
    @SuppressWarnings("unchecked")
    void createReturnsLocationAndCreatedAtFromDatabase() {
        var response = client().post().uri("/users").contentType(MediaType.APPLICATION_JSON)
                .body(body("choi", "choi@example.com", 33)).retrieve().toEntity(Map.class);

        assertThat(response.getStatusCode().value()).isEqualTo(201);
        Map<String, Object> created = response.getBody();
        createdId = ((Number) created.get("id")).longValue();
        assertThat(response.getHeaders().getLocation().getPath()).isEqualTo("/users/" + createdId);
        assertThat(created.get("createdAt")).isNotNull();
    }

    @Test
    @Order(3)
    void duplicateEmailIs409() {
        var response = client().post().uri("/users").contentType(MediaType.APPLICATION_JSON)
                .body(body("other", "choi@example.com", 20)).retrieve().toBodilessEntity();

        assertThat(response.getStatusCode().value()).isEqualTo(409);
    }

    @Test
    @Order(4)
    void invalidRequestIs400() {
        var response = client().post().uri("/users").contentType(MediaType.APPLICATION_JSON)
                .body(body("x", "not-an-email", -5)).retrieve().toBodilessEntity();

        assertThat(response.getStatusCode().value()).isEqualTo(400);
    }

    @Test
    @Order(5)
    @SuppressWarnings("unchecked")
    void updateChangesTheStoredRow() {
        client().put().uri("/users/{id}", createdId).contentType(MediaType.APPLICATION_JSON)
                .body(body("choi2", "choi@example.com", 34)).retrieve().toBodilessEntity();

        Map<String, Object> reloaded = client().get().uri("/users/{id}", createdId).retrieve().body(Map.class);
        assertThat(reloaded.get("name")).isEqualTo("choi2");
        assertThat(reloaded.get("age")).isEqualTo(34);
    }

    @Test
    @Order(6)
    void deleteThenGetIs404() {
        assertThat(client().delete().uri("/users/{id}", createdId).retrieve().toBodilessEntity()
                .getStatusCode().value()).isEqualTo(204);
        assertThat(client().get().uri("/users/{id}", createdId).retrieve().toBodilessEntity()
                .getStatusCode().value()).isEqualTo(404);
    }
}
