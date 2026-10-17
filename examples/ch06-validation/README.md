# ch06. Validation

Jakarta Validation의 제약 애노테이션으로 요청 DTO의 규칙을 선언한다.
설명은 [docs/chapters/ch06](../../docs/chapters/ch06/README.md)에 있다.

## 환경

- JDK 21
- Gradle Wrapper 포함 (별도 설치 불필요)

## 범위

| 항목 | 내용 |
|---|---|
| API | `POST/PUT/PATCH /users` 요청 검증, `GET /users?limit=`, `GET /users/{id}` 인자 검증 |
| 테스트 | 제약 단위 테스트, 검증 실패 400 (15건) |
| 다루지 않는 것 | 오류 응답 형식 통일(ch07), 그룹 검증과 교차 필드 검증 |

## 구조

```text
ch06-validation/
├── build.gradle
└── src/
    ├── main/
    │   ├── java/com/jqdom/curriculum/ch06/
    │   │   ├── AllowedEmailDomain.java
    │   │   ├── AllowedEmailDomainValidator.java
    │   │   ├── Ch06Application.java
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
    └── test/java/com/jqdom/curriculum/ch06/
        ├── Ch06ApplicationTests.java
        ├── ConstraintTests.java
        └── UserValidationApiTests.java
```

## 실행 (저장소 루트에서)

| OS | 명령 |
|---|---|
| Linux / macOS | `./gradlew :examples:ch06-validation:bootRun` |
| Windows | `gradlew.bat :examples:ch06-validation:bootRun` |

## 빌드와 테스트

| OS | 이 챕터만 | 전체 |
|---|---|---|
| Linux / macOS | `./gradlew :examples:ch06-validation:build` | `./gradlew clean build` |
| Windows | `gradlew.bat :examples:ch06-validation:build` | `gradlew.bat clean build` |
