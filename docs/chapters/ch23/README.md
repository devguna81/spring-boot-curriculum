# ch23. Layered Architecture와 SOLID

## 학습 목표

- web, service, repository 계층이 각각 어떤 일만 해야 하는지 구분한다.
- 서비스가 구체 클래스 대신 인터페이스에 의존하게 만들어(DIP) 스프링 없이 가짜 구현으로 테스트한다.
- 업무 예외는 서비스에서 던지고, HTTP 상태 코드로 바꾸는 일은 web 계층에 둔다.
- 계층 규칙(아래 방향으로만 의존)을 ArchUnit 테스트로 고정하고, 규칙이 실제로 위반을 잡는지 확인한다.

## 선수학습

ch09

## 범위

| 항목 | 내용 |
|---|---|
| 예제 | [`examples/ch23-layered-solid`](../../../examples/ch23-layered-solid) |
| API | `POST /api/users`, `GET /api/users/{id}` (이메일 중복 409, 검증 400, 없음 404) |
| 구조 | `web` -> `service` -> `repository`, `notification`, 공통 `domain` |
| 테스트 | 서비스 단위 3건, API 4건, 구조 6건, 컨텍스트 1건 (14건) |
| 다루지 않는 것 | 영속 계층 구현(메모리 저장소 사용), SOLID의 모든 원칙 (아래 표 참고) |

## 이 예제에서 보여 주는 원칙

| 원칙 | 예제에서의 모습 |
|---|---|
| 단일 책임(S) | 컨트롤러는 변환, 서비스는 업무 규칙, 저장소는 저장만 맡는다 |
| 의존 역전(D) | `UserService`는 `UserRepository`, `WelcomeNotifier` 인터페이스만 안다 |
| 인터페이스 분리(I) | 서비스가 필요로 하는 메서드만 가진 작은 인터페이스를 쓴다 |
| 개방-폐쇄(O), 리스코프 치환(L) | 별도 예제를 두지 않는다. 알림 방식을 늘리는 경우는 ch25에서 다룬다 |

## 실행과 테스트

```bash
./gradlew :examples:ch23-layered-solid:test
./gradlew :examples:ch23-layered-solid:bootRun
```

## 리팩터링 전 코드

계층을 어기는 코드는 테스트 소스의 `fixture/violation/FatUserController`에 있습니다. 컨트롤러가 구체 저장소를 직접 만들어 쓰고 업무 규칙까지 안에 둡니다. 어디에도 등록하지 않았고, 계층 규칙이 이 코드를 위반으로 잡는지 확인하는 용도로만 씁니다.

## 다음 챕터

ch24. Contract와 Port/Adapter
