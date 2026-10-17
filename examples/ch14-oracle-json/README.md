# ch14. Oracle JSON

JSON 타입 컬럼과 CLOB + IS JSON 제약의 차이를 설명한다.
설명은 [docs/chapters/ch14](../../docs/chapters/ch14/README.md)에 있다.

## 환경

- JDK 21
- Gradle Wrapper 포함 (별도 설치 불필요)
- Oracle 테스트는 `ORACLE_URL`, `ORACLE_USERNAME`, `ORACLE_PASSWORD` 환경 변수가 있을 때만 실행되고, 없으면 건너뛴다

## 범위

| 항목 | 내용 |
|---|---|
| API | `GET /products/{id}`, `GET /products/{id}/tags`, `GET /products` (oracle Profile) |
| 테스트 | Oracle 전용 질의 (Oracle 10건, 기본 1건) |
| 다루지 않는 것 | JSON 검색 인덱스, JSON Duality View |

## 구조

```text
ch14-oracle-json/
├── build.gradle
└── src/
    ├── main/
    │   ├── java/com/jqdom/curriculum/ch14/
    │   │   ├── Ch14Application.java
    │   │   ├── Product.java
    │   │   ├── ProductController.java
    │   │   └── ProductMapper.java
    │   └── resources/
    │       ├── application-oracle.yml
    │       ├── application.yml
    │       ├── mapper/
    │       │   └── ProductMapper.xml
    │       ├── oracle/
    │       │   └── schema-oracle.sql
    │       └── schema-h2.sql
    └── test/java/com/jqdom/curriculum/ch14/
        ├── Ch14ApplicationTests.java
        └── OracleJsonTests.java
```

## 실행 (저장소 루트에서)

| OS | 명령 |
|---|---|
| Linux / macOS | `./gradlew :examples:ch14-oracle-json:bootRun` |
| Windows | `gradlew.bat :examples:ch14-oracle-json:bootRun` |

## 빌드와 테스트

| OS | 이 챕터만 | 전체 |
|---|---|---|
| Linux / macOS | `./gradlew :examples:ch14-oracle-json:build` | `./gradlew clean build` |
| Windows | `gradlew.bat :examples:ch14-oracle-json:build` | `gradlew.bat clean build` |
