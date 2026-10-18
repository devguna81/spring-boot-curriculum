# ch04. HTTP와 REST, Controller

HTTP 메서드의 의미와 REST의 기본 원칙을 설명한다.
설명은 [docs/chapters/ch04](../../docs/chapters/ch04/README.md)에 있다.

## 환경

- JDK 21
- Gradle Wrapper 포함 (별도 설치 불필요)

## 범위

| 항목 | 내용 |
|---|---|
| API | `GET /users`, `GET /users/{id}` |
| 테스트 | 조회 API의 정상, 404, 400, 405 (9건) |
| 다루지 않는 것 | 생성/수정/삭제(ch05), 검증(ch06), 예외 처리(ch07), 데이터베이스(ch08 이후) |

## 구조

```text
ch04-rest-controller/
├── build.gradle
└── src/
    ├── main/
    │   ├── java/com/jqdom/curriculum/ch04/
    │   │   ├── Ch04Application.java
    │   │   ├── User.java
    │   │   ├── UserController.java
    │   │   └── UserStore.java
    │   └── resources/
    │       └── application.yml
    └── test/java/com/jqdom/curriculum/ch04/
        ├── Ch04ApplicationTests.java
        └── UserControllerTests.java
```

## 실행 (저장소 루트에서)

| OS | 명령 |
|---|---|
| Linux / macOS | `./gradlew :examples:ch04-rest-controller:bootRun` |
| Windows | `gradlew.bat :examples:ch04-rest-controller:bootRun` |

## 빌드와 테스트

| OS | 이 챕터만 | 전체 |
|---|---|---|
| Linux / macOS | `./gradlew :examples:ch04-rest-controller:build` | `./gradlew clean build` |
| Windows | `gradlew.bat :examples:ch04-rest-controller:build` | `gradlew.bat clean build` |
