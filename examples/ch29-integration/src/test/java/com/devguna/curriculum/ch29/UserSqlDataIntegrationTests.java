package com.devguna.curriculum.ch29;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

/**
 * 테스트마다 필요한 데이터를 SQL 파일로 준비하고 끝나면 지운다.
 * @Sql은 자체 트랜잭션에서 실행되어 커밋되므로, 정리 스크립트가 없으면 다른 테스트에 남는다.
 */
@SpringBootTest
@AutoConfigureMockMvc
class UserSqlDataIntegrationTests {

    @Autowired
    MockMvc mvc;

    @Autowired
    UserMapper mapper;

    @Test
    @Sql(scripts = "/sql/extra-users.sql")
    @Sql(scripts = "/sql/cleanup-extra-users.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void preparedRowsAreVisibleThroughTheApi() throws Exception {
        mvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.email=='han@example.com')].age").value(51))
                .andExpect(jsonPath("$[?(@.email=='seo@example.com')].age").value(19));
    }

    @Test
    void preparedRowsAreGoneAfterTheCleanupScript() {
        assertThat(mapper.findAll()).extracting(User::getEmail)
                .doesNotContain("han@example.com", "seo@example.com");
    }
}
