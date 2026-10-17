# ch34. 전략 패턴

조건문 대신 같은 인터페이스를 구현한 전략 여러 개를 두고, 이름(key)으로 골라 쓴다.
설명은 [docs/chapters/ch34](../../docs/chapters/ch34/README.md)에 있다.

## 환경

- JDK 21
- Gradle Wrapper 포함 (별도 설치 불필요)

## 범위

| 항목 | 내용 |
|---|---|
| API | `GET /api/shipping/fee?method=&orderAmount=&weightKg=`, `GET /api/shipping/methods` |
| 전략 | `standard`(50,000원 이상 무료), `express`(2kg 초과분 kg당 500원), `pickup`(무료) |
| 테스트 | 전략 단위 7건, API 4건, 컨텍스트 1건 (12건) |
| 다루지 않는 것 | 전략 선택 기준을 DB로 관리하는 방법, 요청 조건에 따라 자동으로 전략을 고르는 규칙 엔진 |

## 구조

```text
ch34-strategy-pattern/
├── build.gradle
└── src/
    ├── main/
    │   ├── java/com/jqdom/curriculum/ch34/
    │   │   ├── Ch34Application.java
    │   │   ├── ExpressShipping.java
    │   │   ├── PickupShipping.java
    │   │   ├── ShippingController.java
    │   │   ├── ShippingFeeCalculator.java
    │   │   ├── ShippingFeeStrategy.java
    │   │   ├── ShippingRequest.java
    │   │   ├── StandardShipping.java
    │   │   └── UnknownShippingMethodException.java
    │   └── resources/
    │       └── application.yml
    └── test/java/com/jqdom/curriculum/ch34/
        ├── Ch34ApplicationTests.java
        ├── ShippingControllerTests.java
        └── ShippingStrategyTests.java
```

## 실행 (저장소 루트에서)

| OS | 명령 |
|---|---|
| Linux / macOS | `./gradlew :examples:ch34-strategy-pattern:bootRun` |
| Windows | `gradlew.bat :examples:ch34-strategy-pattern:bootRun` |

## 빌드와 테스트

| OS | 이 챕터만 | 전체 |
|---|---|---|
| Linux / macOS | `./gradlew :examples:ch34-strategy-pattern:build` | `./gradlew clean build` |
| Windows | `gradlew.bat :examples:ch34-strategy-pattern:build` | `gradlew.bat clean build` |
