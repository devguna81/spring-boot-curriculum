# ch18. API Contract

외부 계약(contract)과 내부 객체(internal)를 패키지로 분리한다.
설명은 [docs/chapters/ch18](../../docs/chapters/ch18/README.md)에 있다.

## 환경

- JDK 21
- Gradle Wrapper 포함 (별도 설치 불필요)

## 범위

| 항목 | 내용 |
|---|---|
| API | `/api/v1/users` (생성, 목록, 단건) |
| 테스트 | API 테스트, 계약 저장본 비교, 호환성 판정, 의존 방향 (24건) |
| 다루지 않는 것 | 소비자 주도 계약 테스트(Pact), 버전 병행 운영 |

## 구조

```text
ch18-api-contract/
├── build.gradle
└── src/
    ├── main/
    │   ├── java/com/devguna/curriculum/ch18/
    │   │   ├── Ch18Application.java
    │   │   ├── contract/
    │   │   │   ├── CreateUserRequest.java
    │   │   │   ├── ErrorBody.java
    │   │   │   ├── FieldIssue.java
    │   │   │   ├── UserStatus.java
    │   │   │   ├── UserView.java
    │   │   │   └── package-info.java
    │   │   ├── internal/
    │   │   │   ├── DuplicateEmailException.java
    │   │   │   ├── UserApplicationService.java
    │   │   │   ├── UserContractMapper.java
    │   │   │   ├── UserEntity.java
    │   │   │   ├── UserNotFoundException.java
    │   │   │   └── UserRepository.java
    │   │   └── web/
    │   │       ├── GlobalExceptionHandler.java
    │   │       └── UserController.java
    │   └── resources/
    │       └── application.yml
    └── test/
        ├── java/com/devguna/curriculum/ch18/
        │   ├── ArchitectureTests.java
        │   ├── Ch18ApplicationTests.java
        │   ├── ContractDiff.java
        │   ├── ContractDiffTests.java
        │   ├── ContractSnapshotTests.java
        │   ├── UserContractApiTests.java
        │   └── fixture/
        │       ├── contract/
        │       │   └── BadContract.java
        │       └── internal/
        │           └── SomeEntity.java
        └── resources/contract/
            └── openapi-v1.json
```

## 실행 (저장소 루트에서)

| OS | 명령 |
|---|---|
| Linux / macOS | `./gradlew :examples:ch18-api-contract:bootRun` |
| Windows | `gradlew.bat :examples:ch18-api-contract:bootRun` |

## 빌드와 테스트

| OS | 이 챕터만 | 전체 |
|---|---|---|
| Linux / macOS | `./gradlew :examples:ch18-api-contract:build` | `./gradlew clean build` |
| Windows | `gradlew.bat :examples:ch18-api-contract:build` | `gradlew.bat clean build` |
