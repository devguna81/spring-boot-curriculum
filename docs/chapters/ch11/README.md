# ch11. 트랜잭션

## 학습 목표

- @Transactional이 커밋과 롤백의 경계를 정하는 방식을 설명한다.
- 런타임 예외와 체크 예외의 롤백 차이를 확인한다.
- REQUIRES_NEW로 바깥 롤백과 무관하게 남는 기록을 만든다.
- 같은 Bean 안에서 호출하면 @Transactional이 무시되는 이유를 확인한다.

## 선수학습

ch09

## 범위

| 항목 | 내용 |
|---|---|
| 예제 | [`examples/ch11-transaction`](../../../examples/ch11-transaction) |
| API | `POST /transfers`, `GET /accounts/{id}` |
| 테스트 | 이체 성공/실패/롤백 시나리오 (14건) |
| 다루지 않는 것 | 분산 트랜잭션, 격리 수준과 락 |

## 다음 챕터

ch12. MyBatis-Plus로 반복 CRUD 줄이기
