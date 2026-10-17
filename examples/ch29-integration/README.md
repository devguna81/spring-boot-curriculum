# ch29. 통합 테스트

`@SpringBootTest(webEnvironment = RANDOM_PORT)`로 진짜 서버를 띄우고 진짜 HTTP로 시나리오를 검사한다.
설명은 [docs/chapters/ch29](../../docs/chapters/ch29/README.md)에 있다.

## 환경

- JDK 21
- Gradle Wrapper 포함 (별도 설치 불필요)

## 범위

| 항목 | 내용 |
|---|---|
| 테스트 | 시나리오 6건(생성, 중복 409, 검증 400, 수정, 삭제), `@Sql` 2건, 컨텍스트 1건, PostgreSQL 컨테이너 3건 (12건) |
| 다루지 않는 것 | 테스트 컨테이너 재사용 설정, 병렬 테스트 격리, 계약 테스트 |

## 구조

```text
ch29-integration/
├── build.gradle
└── src/
    ├── main/
    │   ├── java/com/jqdom/curriculum/ch29/
    │   │   ├── Ch29Application.java
    │   │   ├── User.java
    │   │   ├── UserController.java
    │   │   ├── UserMapper.java
    │   │   ├── UserRequest.java
    │   │   ├── UserResponse.java
    │   │   └── UserService.java
    │   └── resources/
    │       ├── application.yml
    │       ├── data.sql
    │       ├── mapper/
    │       │   └── UserMapper.xml
    │       └── schema.sql
    └── test/
        ├── java/com/jqdom/curriculum/ch29/
        │   ├── Ch29ApplicationTests.java
        │   ├── PostgresContainerIntegrationTests.java
        │   ├── UserScenarioIntegrationTests.java
        │   └── UserSqlDataIntegrationTests.java
        └── resources/sql/
            ├── cleanup-extra-users.sql
            └── extra-users.sql
```

## 실행 (저장소 루트에서)

| OS | 명령 |
|---|---|
| Linux / macOS | `./gradlew :examples:ch29-integration:bootRun` |
| Windows | `gradlew.bat :examples:ch29-integration:bootRun` |

## 빌드와 테스트

| OS | 이 챕터만 | 전체 |
|---|---|---|
| Linux / macOS | `./gradlew :examples:ch29-integration:build` | `./gradlew clean build` |
| Windows | `gradlew.bat :examples:ch29-integration:build` | `gradlew.bat clean build` |
