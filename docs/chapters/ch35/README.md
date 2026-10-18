# ch35. AOP

## 학습 목표

- 실행 시간 측정처럼 여러 메서드에 반복되는 부가 기능을 Aspect로 분리한다.
- 애노테이션(`@annotation`)으로 대상을 고르는 방법과 패키지(`execution`)로 고르는 방법을 비교한다.
- `@Around`가 결과와 예외를 그대로 전달하면서 기록만 덧붙이는 구조를 확인한다.
- Aspect가 적용된 Bean은 프록시이며, 같은 객체 안에서 `this`로 부르는 메서드에는 적용되지 않는다는 것을 테스트로 확인한다.

## 선수학습

ch03, ch11

## 범위

| 항목 | 내용 |
|---|---|
| 예제 | [`examples/ch35-aop`](../../../examples/ch35-aop) |
| 의존성 | `spring-boot-starter-aspectj` (Boot 4.1.1에는 `spring-boot-starter-aop`가 없다) |
| API | `GET /api/greet`, `GET /api/executions` |
| Aspect | `MeasuredAspect`(`@Measured` 대상, `@Around`), `CallCounter`(service 패키지, `@Before`) |
| 테스트 | Aspect 8건, API 1건, 컨텍스트 1건 (10건) |
| 다루지 않는 것 | `@AfterReturning`/`@AfterThrowing` 세부, 클래스 로드 시점 위빙(AspectJ 에이전트), 순서 지정(`@Order`) |

## 확인하지 않은 것

- Aspect 적용 순서(`@Order`)와 여러 Aspect가 겹칠 때의 동작은 다루지 않았습니다.
- 측정값은 밀리초 정수 내림이라 매우 빠른 메서드는 0으로 기록됩니다.

## 실행과 테스트

```bash
./gradlew :examples:ch35-aop:test
./gradlew :examples:ch35-aop:bootRun
```

## 다음 챕터

ch36. 모듈 호출 오케스트레이션과 온디맨드 배치
