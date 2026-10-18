# ch05. CRUD API와 DTO

POST, GET, PUT, PATCH, DELETE가 CRUD의 어떤 동작에 대응하는지 설명한다.
설명은 [docs/chapters/ch05](../../docs/chapters/ch05/README.md)에 있다.

## 환경

- JDK 21
- Gradle Wrapper 포함 (별도 설치 불필요)

## 범위

| 항목 | 내용 |
|---|---|
| API | `POST/GET/PUT/PATCH/DELETE /users` |
| 테스트 | CRUD 흐름, 멱등성, 404, 415, 400 (12건) |
| 다루지 않는 것 | 입력 검증(ch06), 예외 처리 통일(ch07), 데이터베이스(ch08 이후) |

## 구조

```text
ch05-crud-api/
├── build.gradle
└── src/
    ├── main/
    │   ├── java/com/jqdom/curriculum/ch05/
    │   │   ├── Ch05Application.java
    │   │   ├── CreateUserRequest.java
    │   │   ├── PatchUserRequest.java
    │   │   ├── UpdateUserRequest.java
    │   │   ├── User.java
    │   │   ├── UserController.java
    │   │   ├── UserRepository.java
    │   │   ├── UserResponse.java
    │   │   └── UserService.java
    │   └── resources/
    │       └── application.yml
    └── test/java/com/jqdom/curriculum/ch05/
        ├── Ch05ApplicationTests.java
        ├── UserApiTests.java
        └── UserServiceUnitTests.java
```

## 실행 (저장소 루트에서)

| OS | 명령 |
|---|---|
| Linux / macOS | `./gradlew :examples:ch05-crud-api:bootRun` |
| Windows | `gradlew.bat :examples:ch05-crud-api:bootRun` |

## 빌드와 테스트

| OS | 이 챕터만 | 전체 |
|---|---|---|
| Linux / macOS | `./gradlew :examples:ch05-crud-api:build` | `./gradlew clean build` |
| Windows | `gradlew.bat :examples:ch05-crud-api:build` | `gradlew.bat clean build` |
