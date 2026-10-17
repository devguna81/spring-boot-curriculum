# ch31. Logging과 Actuator

로그 레벨을 패키지별로 정하고, 로그 패턴에 요청 번호(MDC)를 넣는다.
설명은 [docs/chapters/ch31](../../docs/chapters/ch31/README.md)에 있다.

## 환경

- JDK 21
- Gradle Wrapper 포함 (별도 설치 불필요)

## 범위

| 항목 | 내용 |
|---|---|
| 노출 엔드포인트 | `health`, `info`, `metrics`, `loggers` (그 외는 404) |
| 테스트 | Actuator 8건, 로깅 5건, 구조화 로그 1건, 컨텍스트 1건 (15건) |
| 다루지 않는 것 | 로그 수집 시스템(ELK 등), Prometheus 연동, 분산 추적, 엔드포인트 인증 |

## 구조

```text
ch31-logging-actuator/
├── build.gradle
└── src/
    ├── main/
    │   ├── java/com/devguna/curriculum/ch31/
    │   │   ├── Ch31Application.java
    │   │   ├── OrderController.java
    │   │   ├── RequestIdFilter.java
    │   │   ├── StockHealthIndicator.java
    │   │   └── StockSwitch.java
    │   └── resources/
    │       └── application.yml
    └── test/java/com/devguna/curriculum/ch31/
        ├── ActuatorTests.java
        ├── Ch31ApplicationTests.java
        ├── LoggingTests.java
        └── StructuredLoggingTests.java
```

## 실행 (저장소 루트에서)

| OS | 명령 |
|---|---|
| Linux / macOS | `./gradlew :examples:ch31-logging-actuator:bootRun` |
| Windows | `gradlew.bat :examples:ch31-logging-actuator:bootRun` |

## 빌드와 테스트

| OS | 이 챕터만 | 전체 |
|---|---|---|
| Linux / macOS | `./gradlew :examples:ch31-logging-actuator:build` | `./gradlew clean build` |
| Windows | `gradlew.bat :examples:ch31-logging-actuator:build` | `gradlew.bat clean build` |
