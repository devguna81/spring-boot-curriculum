# ch08. DataSource와 H2

DataSource와 커넥션 풀(HikariCP)이 하는 일을 설명한다.
설명은 [docs/chapters/ch08](../../docs/chapters/ch08/README.md)에 있다.

## 환경

- JDK 21
- Gradle Wrapper 포함 (별도 설치 불필요)

## 범위

| 항목 | 내용 |
|---|---|
| API | `GET /users`, `GET /users/{id}`, `GET /db/info` |
| 테스트 | DataSource 설정, 초기 데이터, JdbcClient 조회 (11건) |
| 다루지 않는 것 | 쓰기 쿼리, MyBatis(ch09), 트랜잭션(ch11) |

## 구조

```text
ch08-datasource-h2/
├── build.gradle
└── src/
    ├── main/
    │   ├── java/com/devguna/curriculum/ch08/
    │   │   ├── Ch08Application.java
    │   │   ├── DataSourceInfoController.java
    │   │   ├── User.java
    │   │   ├── UserController.java
    │   │   └── UserRepository.java
    │   └── resources/
    │       ├── application.yml
    │       ├── data.sql
    │       └── schema.sql
    └── test/java/com/devguna/curriculum/ch08/
        ├── Ch08ApplicationTests.java
        ├── DataSourceTests.java
        ├── UserApiTests.java
        └── UserRepositoryTests.java
```

## 실행 (저장소 루트에서)

| OS | 명령 |
|---|---|
| Linux / macOS | `./gradlew :examples:ch08-datasource-h2:bootRun` |
| Windows | `gradlew.bat :examples:ch08-datasource-h2:bootRun` |

## 빌드와 테스트

| OS | 이 챕터만 | 전체 |
|---|---|---|
| Linux / macOS | `./gradlew :examples:ch08-datasource-h2:build` | `./gradlew clean build` |
| Windows | `gradlew.bat :examples:ch08-datasource-h2:build` | `gradlew.bat clean build` |
