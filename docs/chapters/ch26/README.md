# ch26. Modular Monolith

## 학습 목표

- 하나의 애플리케이션 안에서 `member`, `catalog`, `order`, `reward` 모듈을 나누고, 각 모듈은 `api`(공개)와 `internal`(비공개) 패키지를 가진다.
- 다른 모듈은 `api`로만 접근하게 하고, `internal` 클래스를 패키지 전용(package-private)으로 둔다.
- 모듈 사이의 순환 의존이 생기면 빌드가 실패하도록 ArchUnit으로 막는다.
- 모듈 사이의 연결을 인터페이스 호출(동기)과 이벤트(주문 -> 적립)로 나누고, 이벤트가 트랜잭션 커밋 이후에만 전달되는 것을 테스트로 확인한다.

## 선수학습

ch11, ch25

## 범위

| 항목 | 내용 |
|---|---|
| 예제 | [`examples/ch26-modular-monolith`](../../../examples/ch26-modular-monolith) |
| API | `POST /api/orders` (회원 id, 상품 코드, 수량. 없는 회원/상품 404, 입력 오류 400) |
| 모듈 의존 | `order` -> `member.api`, `catalog.api` / `reward` -> `order.api` (순환 없음) |
| 저장소 | H2 인메모리. 테이블은 모듈이 하나씩 소유한다(`member`, `orders`, `reward`) |
| 테스트 | 구조 8건, 주문 흐름 5건, 트랜잭션 경계 3건, 컨텍스트 1건 (17건) |
| 다루지 않는 것 | Spring Modulith 같은 모듈 전용 라이브러리, 모듈별 Gradle 서브프로젝트 분리, 이벤트 영속화와 재전송 |

## 의존 방향이 순환이 되는 예

주문이 회원을 확인하려고 `member.api`를 부르는데, 회원 모듈이 주문 이벤트를 듣기 위해 `order.api`를 참조하면 두 모듈이 서로를 가리키는 순환이 됩니다. 이 예제는 적립(`reward`)을 별도 모듈로 두어 `order`가 `reward`를 모르는 채 `reward`만 `order.api`를 향하게 했습니다.

## 트랜잭션 경계에서 확인한 것

| 상황 | 결과 (테스트로 확인) |
|---|---|
| 주문이 커밋되기 전 | 적립은 일어나지 않는다 |
| 주문이 커밋된 뒤 | 적립이 반영된다 |
| 바깥 트랜잭션이 롤백됨 | 주문 행도 사라지고 적립 리스너도 호출되지 않는다 (`@Transactional`이 바깥 트랜잭션에 참여) |

적립 리스너(`@TransactionalEventListener(AFTER_COMMIT)`)에는 `REQUIRES_NEW`를 붙였습니다. Spring의 `TransactionSynchronization.afterCommit` Javadoc이 이 시점의 데이터 접근에 새 트랜잭션을 쓰라고 안내하기 때문입니다. 이 예제에서는 어노테이션을 빼도 값이 반영되는 것을 확인했으며, 그 이유는 확인하지 않았습니다.

## 확인하지 않은 것

- 적립 리스너가 실패했을 때 주문이 어떻게 되는지(커밋 이후라 주문은 남고 적립만 빠진다)는 테스트하지 않았습니다. 실무에서는 재처리 장치가 필요합니다.
- 적립 갱신(UPDATE 후 INSERT)은 같은 회원의 동시 주문에서 경합할 수 있으며 검증하지 않았습니다.
- 모듈별 테이블 소유는 관례이며, SQL 수준에서 다른 모듈 테이블 접근을 막는 테스트는 없습니다.

## 실행과 테스트

```bash
./gradlew :examples:ch26-modular-monolith:test
./gradlew :examples:ch26-modular-monolith:bootRun
```

## 다음 챕터

ch27. JUnit과 Mockito (Part 07)
