# ch11. 트랜잭션

@Transactional이 커밋과 롤백의 경계를 정하는 방식을 설명한다.
설명은 [docs/chapters/ch11](../../docs/chapters/ch11/README.md)에 있다.

## 환경

- JDK 21
- Gradle Wrapper 포함 (별도 설치 불필요)

## 범위

| 항목 | 내용 |
|---|---|
| API | `POST /transfers`, `GET /accounts/{id}` |
| 테스트 | 이체 성공/실패/롤백 시나리오 (14건) |
| 다루지 않는 것 | 분산 트랜잭션, 격리 수준과 락 |

## 구조

```text
ch11-transaction/
├── build.gradle
└── src/
    ├── main/
    │   ├── java/com/jqdom/curriculum/ch11/
    │   │   ├── Account.java
    │   │   ├── AccountMapper.java
    │   │   ├── AccountNotFoundException.java
    │   │   ├── AuditMapper.java
    │   │   ├── AuditService.java
    │   │   ├── Ch11Application.java
    │   │   ├── InsufficientFundsException.java
    │   │   ├── TransferController.java
    │   │   └── TransferService.java
    │   └── resources/
    │       ├── application.yml
    │       ├── data.sql
    │       └── schema.sql
    └── test/java/com/jqdom/curriculum/ch11/
        ├── Ch11ApplicationTests.java
        ├── TransactionTests.java
        └── TransferApiTests.java
```

## 실행 (저장소 루트에서)

| OS | 명령 |
|---|---|
| Linux / macOS | `./gradlew :examples:ch11-transaction:bootRun` |
| Windows | `gradlew.bat :examples:ch11-transaction:bootRun` |

## 빌드와 테스트

| OS | 이 챕터만 | 전체 |
|---|---|---|
| Linux / macOS | `./gradlew :examples:ch11-transaction:build` | `./gradlew clean build` |
| Windows | `gradlew.bat :examples:ch11-transaction:build` | `gradlew.bat clean build` |
