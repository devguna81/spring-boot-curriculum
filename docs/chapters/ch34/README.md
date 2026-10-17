# ch34. 전략 패턴

## 학습 목표

- 조건문 대신 같은 인터페이스를 구현한 전략 여러 개를 두고, 이름(key)으로 골라 쓴다.
- 스프링이 주입하는 `List<ShippingFeeStrategy>`로 전략 목록을 만들고, 새 전략을 계산기 수정 없이 추가한다.
- 같은 key가 둘이면 기동이 실패하고, 없는 key는 사용 가능한 목록과 함께 거절되는 것을 확인한다.
- 전략 패턴과 Composite(ch25)의 차이를 코드로 비교한다.

## 선수학습

ch03, ch25

## 범위

| 항목 | 내용 |
|---|---|
| 예제 | [`examples/ch34-strategy-pattern`](../../../examples/ch34-strategy-pattern) |
| API | `GET /api/shipping/fee?method=&orderAmount=&weightKg=`, `GET /api/shipping/methods` |
| 전략 | `standard`(50,000원 이상 무료), `express`(2kg 초과분 kg당 500원), `pickup`(무료) |
| 테스트 | 전략 단위 7건, API 4건, 컨텍스트 1건 (12건) |
| 다루지 않는 것 | 전략 선택 기준을 DB로 관리하는 방법, 요청 조건에 따라 자동으로 전략을 고르는 규칙 엔진 |

## 구조

```text
ShippingController -> ShippingFeeCalculator -> ShippingFeeStrategy (인터페이스)
                                                 ├─ StandardShipping
                                                 ├─ ExpressShipping
                                                 └─ PickupShipping
```

계산기는 전략을 key로 찾아 `fee()`만 부릅니다. 배송 방법별 `if/else`가 없으므로 방법이 늘어도 계산기는 바뀌지 않습니다.

## 확인하지 않은 것

- 요금 정책의 현실성(실제 배송사 요금)은 다루지 않았습니다. 금액은 설명용 값입니다.

## 실행과 테스트

```bash
./gradlew :examples:ch34-strategy-pattern:test
./gradlew :examples:ch34-strategy-pattern:bootRun
```

## 다음 챕터

ch35. AOP
