# ch23. Layered Architecture와 SOLID

web, service, repository 계층이 각각 어떤 일만 해야 하는지 구분한다.
설명은 [docs/chapters/ch23](../../docs/chapters/ch23/README.md)에 있다.

## 환경

- JDK 21
- Gradle Wrapper 포함 (별도 설치 불필요)

## 범위

| 항목 | 내용 |
|---|---|
| API | `POST /api/users`, `GET /api/users/{id}` (이메일 중복 409, 검증 400, 없음 404) |
| 구조 | `web` -> `service` -> `repository`, `notification`, 공통 `domain` |
| 테스트 | 서비스 단위 3건, API 4건, 구조 6건, 컨텍스트 1건 (14건) |
| 다루지 않는 것 | 영속 계층 구현(메모리 저장소 사용), SOLID의 모든 원칙 (아래 표 참고) |

## 구조

```text
ch23-layered-solid/
├── build.gradle
└── src/
    ├── main/
    │   ├── java/com/jqdom/curriculum/ch23/
    │   │   ├── Ch23Application.java
    │   │   ├── domain/
    │   │   │   └── User.java
    │   │   ├── notification/
    │   │   │   ├── LogWelcomeNotifier.java
    │   │   │   └── WelcomeNotifier.java
    │   │   ├── repository/
    │   │   │   ├── InMemoryUserRepository.java
    │   │   │   └── UserRepository.java
    │   │   ├── service/
    │   │   │   ├── DuplicateEmailException.java
    │   │   │   ├── UserNotFoundException.java
    │   │   │   └── UserService.java
    │   │   └── web/
    │   │       ├── GlobalExceptionHandler.java
    │   │       ├── UserController.java
    │   │       ├── UserRequest.java
    │   │       └── UserResponse.java
    │   └── resources/
    │       └── application.yml
    └── test/java/com/jqdom/curriculum/ch23/
        ├── ArchitectureTests.java
        ├── Ch23ApplicationTests.java
        ├── UserApiTests.java
        ├── UserServiceTests.java
        └── fixture/violation/
            └── FatUserController.java
```

## 실행 (저장소 루트에서)

| OS | 명령 |
|---|---|
| Linux / macOS | `./gradlew :examples:ch23-layered-solid:bootRun` |
| Windows | `gradlew.bat :examples:ch23-layered-solid:bootRun` |

## 빌드와 테스트

| OS | 이 챕터만 | 전체 |
|---|---|---|
| Linux / macOS | `./gradlew :examples:ch23-layered-solid:build` | `./gradlew clean build` |
| Windows | `gradlew.bat :examples:ch23-layered-solid:build` | `gradlew.bat clean build` |
