# ch25. Composite와 Orchestration

## 학습 목표

- 단일 규칙과 규칙 묶음을 같은 인터페이스로 다루는 Composite 패턴을 할인 규칙에 적용한다.
- 합산(`sumOf`)과 최대값 선택(`bestOf`)을 중첩해 "VIP는 항상, 나머지는 큰 것 하나만" 같은 정책을 트리로 표현한다.
- 재고, 결제, 배송 서비스를 한 클래스(Orchestrator)가 순서대로 부르고, 실패하면 앞 단계를 반대 순서로 되돌린다.
- 호출 순서를 기록하는 가짜 서비스로 흐름과 보상 순서를 테스트한다.

## 선수학습

ch24

## 범위

| 항목 | 내용 |
|---|---|
| 예제 | [`examples/ch25-composite-orchestration`](../../../examples/ch25-composite-orchestration) |
| API | `POST /api/orders` (성공 201, 재고 부족 409, 결제 거절 402, 입력 오류 400) |
| 할인 정책 | `sumOf(VIP 10%, bestOf(5만 원 이상 3천 원, 첫 주문 2천 원))`, 합계는 주문 금액을 넘지 않는다 |
| 테스트 | Composite 6건, Orchestrator 4건, API 4건, 컨텍스트 1건 (15건) |
| 다루지 않는 것 | 분산 트랜잭션과 Saga 프레임워크, 보상 작업 자체의 실패 처리, 재시도, 메시지 큐 |

## 흐름

```text
재고 확보 -> 결제 -> 배송 예약
  재고 실패:  (되돌릴 것 없음)
  결제 실패:  재고 반환
  배송 실패:  결제 취소 -> 재고 반환   (일어난 순서의 반대)
```

각 서비스는 서로를 부르지 않습니다. 순서와 실패 시 처리는 `OrderOrchestrator` 한곳에만 있습니다.

## 확인하지 않은 것

- 배송 실패가 HTTP 응답에서 500으로 나가는 경로는 API 테스트로 확인하지 않았습니다. 보상 순서는 Orchestrator 단위 테스트로 확인했습니다.
- 보상(결제 취소, 재고 반환) 자체가 실패하면 어떻게 되는지는 구현하지 않았습니다. 실무에서는 재시도나 수동 처리 대상으로 남기는 설계가 필요합니다.
- 인메모리 서비스는 동시 요청에서의 정확성을 검증하지 않았습니다.

## 실행과 테스트

```bash
./gradlew :examples:ch25-composite-orchestration:test
./gradlew :examples:ch25-composite-orchestration:bootRun
```

## 다음 챕터

ch26. Modular Monolith
