# ch27. JUnit과 Mockito

## 학습 목표

- JUnit Jupiter의 `@Test`, `@Nested`, `@DisplayName`, `@ParameterizedTest`로 테스트를 읽기 쉽게 구성한다.
- 협력 객체(저장소, 메일 발송)를 Mockito 목으로 바꿔 서비스 규칙만 검사한다.
- `when/given`, `verify/then`, `ArgumentCaptor`, `InOrder`로 "무엇이 어떤 값으로 어떤 순서로 불렸는가"를 확인한다.
- 현재 시간을 `Clock`으로 받아 시간 의존 로직을 고정된 시각에서 검사한다.
- 엄격 모드(strict stubs)가 잘못된 stub을 잡는 것을 확인한다.

## 선수학습

ch05

## 범위

| 항목 | 내용 |
|---|---|
| 예제 | [`examples/ch27-junit-mockito`](../../../examples/ch27-junit-mockito) |
| 대상 | `SignupService`(이메일 정규화, 중복 거절, 저장 후 메일 발송), `DiscountPolicy`(가입 기간별 할인율) |
| 테스트 | `SignupServiceTests` 6건, `DiscountPolicyTests` 7건(매개변수 6건 포함) (13건) |
| 특징 | 스프링 컨테이너를 쓰지 않는다. 실행 가능한 애플리케이션이 아니라서 `bootJar`는 끈다 |
| 다루지 않는 것 | 정적 메서드 목, PowerMock, 변경 테스트(mutation testing) 도구 |

## 확인한 것

- 규칙 두 곳(이메일 소문자 변환, 3년 경계 `>=`)을 일부러 틀리게 고쳐 보면 해당 테스트가 실패한다.
- 쓰이지 않는 stub이나 인자가 다른 stub은 엄격 모드에서 `PotentialStubbingProblem`으로 실패한다. 테스트가 "일부러 놓친" 호출을 숨기지 못한다.
- 메일 발송이 실패해도 이미 저장된 사용자는 되돌려지지 않는다. 테스트 `mailFailurePropagatesAfterTheUserWasSaved`가 이 현재 동작을 드러낸다.

## 실행

```bash
./gradlew :examples:ch27-junit-mockito:test
```

## 다음 챕터

ch28. 계층별 테스트
