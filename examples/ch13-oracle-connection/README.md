# ch13. Oracle 연결

기본 Profile(H2)과 oracle Profile을 분리한다.
설명은 [docs/chapters/ch13](../../docs/chapters/ch13/README.md)에 있다.

## 환경

- JDK 21
- Gradle Wrapper 포함 (별도 설치 불필요)
- Oracle 테스트는 `ORACLE_URL`, `ORACLE_USERNAME`, `ORACLE_PASSWORD` 환경 변수가 있을 때만 실행되고, 없으면 건너뛴다

## 범위

| 항목 | 내용 |
|---|---|
| API | `GET /db/info` |
| 테스트 | H2 기본 테스트와 Oracle 전용 테스트 (H2 4건, Oracle 6건) |
| 다루지 않는 것 | Oracle RAC, Wallet, 커넥션 풀 튜닝 |

## 구조

```text
ch13-oracle-connection/
├── build.gradle
├── compose.yml
└── src/
    ├── main/
    │   ├── java/com/devguna/curriculum/ch13/
    │   │   ├── Ch13Application.java
    │   │   ├── DbInfoController.java
    │   │   ├── User.java
    │   │   └── UserMapper.java
    │   └── resources/
    │       ├── application-oracle.yml
    │       ├── application.yml
    │       ├── data-h2.sql
    │       ├── mapper/
    │       │   └── UserMapper.xml
    │       ├── oracle/
    │       │   └── schema-oracle.sql
    │       └── schema-h2.sql
    └── test/java/com/devguna/curriculum/ch13/
        ├── Ch13ApplicationTests.java
        ├── H2ProfileTests.java
        └── OracleConnectionTests.java
```

## 실행 (저장소 루트에서)

| OS | 명령 |
|---|---|
| Linux / macOS | `./gradlew :examples:ch13-oracle-connection:bootRun` |
| Windows | `gradlew.bat :examples:ch13-oracle-connection:bootRun` |

## 빌드와 테스트

| OS | 이 챕터만 | 전체 |
|---|---|---|
| Linux / macOS | `./gradlew :examples:ch13-oracle-connection:build` | `./gradlew clean build` |
| Windows | `gradlew.bat :examples:ch13-oracle-connection:build` | `gradlew.bat clean build` |
