# ch25. Composite와 Orchestration

단일 규칙과 규칙 묶음을 같은 인터페이스로 다루는 Composite 패턴을 할인 규칙에 적용한다.
설명은 [docs/chapters/ch25](../../docs/chapters/ch25/README.md)에 있다.

## 환경

- JDK 21
- Gradle Wrapper 포함 (별도 설치 불필요)

## 범위

| 항목 | 내용 |
|---|---|
| API | `POST /api/orders` (성공 201, 재고 부족 409, 결제 거절 402, 입력 오류 400) |
| 할인 정책 | `sumOf(VIP 10%, bestOf(5만 원 이상 3천 원, 첫 주문 2천 원))`, 합계는 주문 금액을 넘지 않는다 |
| 테스트 | Composite 6건, Orchestrator 4건, API 4건, 컨텍스트 1건 (15건) |
| 다루지 않는 것 | 분산 트랜잭션과 Saga 프레임워크, 보상 작업 자체의 실패 처리, 재시도, 메시지 큐 |

## 구조

```text
ch25-composite-orchestration/
├── build.gradle
└── src/
    ├── main/
    │   ├── java/com/devguna/curriculum/ch25/
    │   │   ├── Ch25Application.java
    │   │   ├── inventory/
    │   │   │   ├── InMemoryInventoryService.java
    │   │   │   ├── InventoryService.java
    │   │   │   └── OutOfStockException.java
    │   │   ├── order/
    │   │   │   ├── OrderFailedException.java
    │   │   │   ├── OrderOrchestrator.java
    │   │   │   ├── OrderResult.java
    │   │   │   ├── PlaceOrderCommand.java
    │   │   │   └── PricingConfig.java
    │   │   ├── payment/
    │   │   │   ├── InMemoryPaymentService.java
    │   │   │   ├── PaymentDeclinedException.java
    │   │   │   └── PaymentService.java
    │   │   ├── pricing/
    │   │   │   ├── BulkAmountRule.java
    │   │   │   ├── CompositeDiscountRule.java
    │   │   │   ├── DiscountRule.java
    │   │   │   ├── FirstOrderRule.java
    │   │   │   ├── Grade.java
    │   │   │   ├── PricingContext.java
    │   │   │   └── VipRateRule.java
    │   │   ├── shipping/
    │   │   │   ├── InMemoryShippingService.java
    │   │   │   └── ShippingService.java
    │   │   └── web/
    │   │       ├── OrderController.java
    │   │       └── OrderRequest.java
    │   └── resources/
    │       └── application.yml
    └── test/java/com/devguna/curriculum/ch25/
        ├── Ch25ApplicationTests.java
        ├── CompositeDiscountRuleTests.java
        ├── OrderApiTests.java
        └── OrderOrchestratorTests.java
```

## 실행 (저장소 루트에서)

| OS | 명령 |
|---|---|
| Linux / macOS | `./gradlew :examples:ch25-composite-orchestration:bootRun` |
| Windows | `gradlew.bat :examples:ch25-composite-orchestration:bootRun` |

## 빌드와 테스트

| OS | 이 챕터만 | 전체 |
|---|---|---|
| Linux / macOS | `./gradlew :examples:ch25-composite-orchestration:build` | `./gradlew clean build` |
| Windows | `gradlew.bat :examples:ch25-composite-orchestration:build` | `gradlew.bat clean build` |
