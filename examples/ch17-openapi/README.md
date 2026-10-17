# ch17. OpenAPI와 Swagger UI

springdoc이 컨트롤러에서 OpenAPI 문서를 만드는 방식을 설명한다.
설명은 [docs/chapters/ch17](../../docs/chapters/ch17/README.md)에 있다.

## 환경

- JDK 21
- Gradle Wrapper 포함 (별도 설치 불필요)

## 범위

| 항목 | 내용 |
|---|---|
| API | `/users` CRUD, `/v3/api-docs`, `/swagger-ui.html` |
| 테스트 | ch07 동작 테스트 + 문서 내용 테스트 (29건) |
| 다루지 않는 것 | 인증 스키마 문서화, 문서 기반 클라이언트 코드 생성 |

## 구조

```text
ch17-openapi/
├── build.gradle
└── src/
    ├── main/
    │   ├── java/com/jqdom/curriculum/ch17/
    │   │   ├── AllowedEmailDomain.java
    │   │   ├── AllowedEmailDomainValidator.java
    │   │   ├── ApiError.java
    │   │   ├── ApiResponse.java
    │   │   ├── BusinessException.java
    │   │   ├── Ch17Application.java
    │   │   ├── CreateUserRequest.java
    │   │   ├── DemoController.java
    │   │   ├── DuplicateEmailException.java
    │   │   ├── ErrorCode.java
    │   │   ├── GlobalExceptionHandler.java
    │   │   ├── OpenApiConfig.java
    │   │   ├── PatchUserRequest.java
    │   │   ├── UpdateUserRequest.java
    │   │   ├── User.java
    │   │   ├── UserController.java
    │   │   ├── UserNotFoundException.java
    │   │   ├── UserRepository.java
    │   │   ├── UserResponse.java
    │   │   └── UserService.java
    │   └── resources/
    │       ├── application-prod.yml
    │       └── application.yml
    └── test/java/com/jqdom/curriculum/ch17/
        ├── ApiResponseTests.java
        ├── Ch17ApplicationTests.java
        ├── GlobalExceptionHandlerTests.java
        ├── OpenApiDisabledInProdTests.java
        └── OpenApiDocsTests.java
```

## 실행 (저장소 루트에서)

| OS | 명령 |
|---|---|
| Linux / macOS | `./gradlew :examples:ch17-openapi:bootRun` |
| Windows | `gradlew.bat :examples:ch17-openapi:bootRun` |

## 빌드와 테스트

| OS | 이 챕터만 | 전체 |
|---|---|---|
| Linux / macOS | `./gradlew :examples:ch17-openapi:build` | `./gradlew clean build` |
| Windows | `gradlew.bat :examples:ch17-openapi:build` | `gradlew.bat clean build` |
