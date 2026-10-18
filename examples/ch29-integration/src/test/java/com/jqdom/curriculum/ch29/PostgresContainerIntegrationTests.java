package com.jqdom.curriculum.ch29;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import javax.sql.DataSource;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

/**
 * H2 대신 실제 PostgreSQL 컨테이너에 연결한다. Docker가 없으면 이 클래스는 건너뛴다.
 * @ServiceConnection이 컨테이너의 접속 정보를 DataSource 설정으로 연결해 주므로 URL을 적지 않는다.
 * schema.sql과 data.sql은 임베디드 DB(H2)에서만 자동 실행되는 것이 기본값이라, 실제 DB에서는
 * spring.sql.init.mode=always를 주어야 같은 스크립트가 실행된다.
 */
@Testcontainers(disabledWithoutDocker = true)
@SpringBootTest(properties = "spring.sql.init.mode=always")
@Transactional
class PostgresContainerIntegrationTests {

    @Container
    @ServiceConnection
    static PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:17-alpine");

    @Autowired
    UserMapper mapper;

    @Autowired
    DataSource dataSource;

    @Autowired
    JdbcClient jdbc;

    @Test
    void runsAgainstARealPostgreSql() {
        String version = jdbc.sql("SELECT version()").query(String.class).single();

        assertThat(version).startsWith("PostgreSQL 17");
    }

    @Test
    void mapperWorksWithTheSameSqlAsOnH2() {
        assertThat(mapper.findAll()).extracting(User::getName).containsExactly("kim", "lee", "park");

        User user = new User("choi", "choi@example.com", 30);
        mapper.insert(user);
        assertThat(mapper.findById(user.getId()).getCreatedAt()).isNotNull();
    }

    @Test
    void uniqueConstraintIsTranslatedTheSameWay() {
        assertThatThrownBy(() -> mapper.insert(new User("other", "kim@example.com", 20)))
                .isInstanceOf(DuplicateKeyException.class);
    }
}
