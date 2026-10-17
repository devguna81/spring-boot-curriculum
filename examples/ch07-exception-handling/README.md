# ch07. 예외 처리와 공통 응답

서비스에서 의미 있는 예외를 던지고 `@RestControllerAdvice`에서 한곳에서 처리한다.
설명은 [docs/chapters/ch07](../../docs/chapters/ch07/README.md)에 있다.

## 환경

- JDK 21
- Gradle Wrapper 포함 (별도 설치 불필요)

## 범위

| 항목 | 내용 |
|---|---|
| API | `/users` CRUD (공통 응답), `GET /demo/error` |
| 테스트 | 예외 종류별 응답 형식과 상태 코드 (16건) |
| 다루지 않는 것 | 인증/인가 오류(ch30), 오류 메시지 다국어, ProblemDetail(RFC 9457) 표준 응답 |

## 구조

```text
ch07-exception-handling/
├── build.gradle
└── src/
    ├── main/
    │   ├── java/com/devguna/curriculum/ch07/
    │   │   ├── AllowedEmailDomain.java
    │   │   ├── AllowedEmailDomainValidator.java
    │   │   ├── ApiError.java
    │   │   ├── ApiResponse.java
    │   │   ├── BusinessException.java
    │   │   ├── Ch07Application.java
    │   │   ├── CreateUserRequest.java
    │   │   ├── DemoController.java
    │   │   ├── DuplicateEmailException.java
    │   │   ├── ErrorCode.java
    │   │   ├── GlobalExceptionHandler.java
    │   │   ├── PatchUserRequest.java
    │   │   ├── UpdateUserRequest.java
    │   │   ├── User.java
    │   │   ├── UserController.java
    │   │   ├── UserNotFoundException.java
    │   │   ├── UserRepository.java
    │   │   ├── UserResponse.java
    │   │   └── UserService.java
    │   └── resources/
    │       └── application.yml
    └── test/java/com/devguna/curriculum/ch07/
        ├── ApiResponseTests.java
        ├── Ch07ApplicationTests.java
        └── GlobalExceptionHandlerTests.java
```

## 실행 (저장소 루트에서)

| OS | 명령 |
|---|---|
| Linux / macOS | `./gradlew :examples:ch07-exception-handling:bootRun` |
| Windows | `gradlew.bat :examples:ch07-exception-handling:bootRun` |

## 빌드와 테스트

| OS | 이 챕터만 | 전체 |
|---|---|---|
| Linux / macOS | `./gradlew :examples:ch07-exception-handling:build` | `./gradlew clean build` |
| Windows | `gradlew.bat :examples:ch07-exception-handling:build` | `gradlew.bat clean build` |
