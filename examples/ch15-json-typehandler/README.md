# ch15. JSON과 Java 객체 변환

Jackson으로 객체와 JSON 문자열을 오간다.
설명은 [docs/chapters/ch15](../../docs/chapters/ch15/README.md)에 있다.

## 환경

- JDK 21
- Gradle Wrapper 포함 (별도 설치 불필요)
- Oracle 테스트는 `ORACLE_URL`, `ORACLE_USERNAME`, `ORACLE_PASSWORD` 환경 변수가 있을 때만 실행되고, 없으면 건너뛴다

## 범위

| 항목 | 내용 |
|---|---|
| API | `POST /products`, `GET /products/{id}` |
| 테스트 | TypeHandler 단위 테스트, H2/Oracle 왕복 (H2 14건, Oracle 3건) |
| 다루지 않는 것 | JSON 스키마 검증, 다형 타입 직렬화 |

## 구조

```text
ch15-json-typehandler/
├── build.gradle
└── src/
    ├── main/
    │   ├── java/com/jqdom/curriculum/ch15/
    │   │   ├── Ch15Application.java
    │   │   ├── JsonTypeHandler.java
    │   │   ├── Product.java
    │   │   ├── ProductAttributes.java
    │   │   ├── ProductAttributesTypeHandler.java
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
    └── test/java/com/jqdom/curriculum/ch15/
        ├── Ch15ApplicationTests.java
        ├── JsonTypeHandlerUnitTests.java
        ├── OracleJsonColumnTests.java
        ├── ProductApiTests.java
        └── ProductMapperTests.java
```

## 실행 (저장소 루트에서)

| OS | 명령 |
|---|---|
| Linux / macOS | `./gradlew :examples:ch15-json-typehandler:bootRun` |
| Windows | `gradlew.bat :examples:ch15-json-typehandler:bootRun` |

## 빌드와 테스트

| OS | 이 챕터만 | 전체 |
|---|---|---|
| Linux / macOS | `./gradlew :examples:ch15-json-typehandler:build` | `./gradlew clean build` |
| Windows | `gradlew.bat :examples:ch15-json-typehandler:build` | `gradlew.bat clean build` |
