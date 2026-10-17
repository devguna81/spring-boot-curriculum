# ch28. 계층별 테스트

`@WebMvcTest`로 웹 계층만 띄우고 서비스는 `@MockitoBean`으로 바꿔 검사한다.
설명은 [docs/chapters/ch28](../../docs/chapters/ch28/README.md)에 있다.

## 환경

- JDK 21
- Gradle Wrapper 포함 (별도 설치 불필요)

## 범위

| 항목 | 내용 |
|---|---|
| 테스트 | 웹 7건, 서비스 3건, 매퍼 6건, 전체 컨텍스트 1건 (17건) |
| 다루지 않는 것 | `@DataJpaTest` 등 JPA 슬라이스, 슬라이스 테스트의 속도 측정 |

## 구조

```text
ch28-slice-tests/
├── build.gradle
└── src/
    ├── main/
    │   ├── java/com/jqdom/curriculum/ch28/
    │   │   ├── Ch28Application.java
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
    └── test/java/com/jqdom/curriculum/ch28/
        ├── Ch28ApplicationTests.java
        ├── UserControllerWebTests.java
        ├── UserMapperSliceTests.java
        └── UserServiceTests.java
```

## 실행 (저장소 루트에서)

| OS | 명령 |
|---|---|
| Linux / macOS | `./gradlew :examples:ch28-slice-tests:bootRun` |
| Windows | `gradlew.bat :examples:ch28-slice-tests:bootRun` |

## 빌드와 테스트

| OS | 이 챕터만 | 전체 |
|---|---|---|
| Linux / macOS | `./gradlew :examples:ch28-slice-tests:build` | `./gradlew clean build` |
| Windows | `gradlew.bat :examples:ch28-slice-tests:build` | `gradlew.bat clean build` |
