# ch29. 통합 테스트

## 학습 목표

- `@SpringBootTest(webEnvironment = RANDOM_PORT)`로 진짜 서버를 띄우고 진짜 HTTP로 시나리오를 검사한다.
- `@Sql`로 테스트마다 필요한 데이터를 준비하고 정리한다.
- Testcontainers로 실제 PostgreSQL 컨테이너에 연결해, H2에서 통과하던 코드가 실제 DB에서도 동작하는지 확인한다.
- Docker가 없는 환경에서는 컨테이너 테스트를 건너뛰도록 한다.

## 선수학습

ch28

## 범위

| 항목 | 내용 |
|---|---|
| 예제 | [`examples/ch29-integration`](../../../examples/ch29-integration) |
| 테스트 | 시나리오 6건(생성, 중복 409, 검증 400, 수정, 삭제), `@Sql` 2건, 컨텍스트 1건, PostgreSQL 컨테이너 3건 (12건) |
| 다루지 않는 것 | 테스트 컨테이너 재사용 설정, 병렬 테스트 격리, 계약 테스트 |

## 알아 둘 점

- `schema.sql`, `data.sql`은 임베디드 DB(H2)에서만 자동 실행되는 것이 기본값이다. 컨테이너의 PostgreSQL에서는 `spring.sql.init.mode=always`를 주어야 같은 스크립트가 실행된다. 처음에는 이를 빠뜨려 `relation "users" does not exist`로 실패했다.
- `@Sql`은 자체 트랜잭션에서 실행되어 커밋된다. 정리 스크립트(`executionPhase = AFTER_TEST_METHOD`)가 없으면 다른 테스트에 데이터가 남는다.
- 시나리오 테스트는 단계 사이가 서로의 결과에 기대므로 실행 순서를 고정한다. 단계를 독립시키기 어려운 흐름에서만 쓴다.

## Docker가 필요한 테스트

`PostgresContainerIntegrationTests`는 Docker가 없으면 건너뛴다(`disabledWithoutDocker = true`). GitHub Actions의 `ubuntu-latest`에서는 Docker가 있어 실행된다. Docker 안에서 Gradle을 실행하는 환경에서는 컨테이너 포트 접근이 막혀 이 클래스를 직접 실행하지 못할 수 있다.

## 실행

```bash
./gradlew :examples:ch29-integration:test
```

## 다음 챕터

ch30. Spring Security
