# ch07. 예외 처리와 공통 응답

## 학습 목표

- 서비스에서 의미 있는 예외를 던지고 `@RestControllerAdvice`에서 한곳에서 처리한다.
- 성공과 실패에 같은 모양의 공통 응답(`ApiResponse`)을 사용한다.
- 오류 코드(`ErrorCode`)로 오류 종류를 계약으로 정의한다.
- 검증 실패, 깨진 JSON, 형식 오류, 405, 415, 404를 같은 형식으로 응답한다.
- 예상하지 못한 예외의 내부 메시지를 응답에서 감춘다.

## 선수학습

ch05, ch06

## 범위

| 항목 | 내용 |
|---|---|
| 예제 | [`examples/ch07-exception-handling`](../../../examples/ch07-exception-handling) |
| API | `/users` CRUD (공통 응답), `GET /demo/error` |
| 테스트 | 예외 종류별 응답 형식과 상태 코드 (16건) |
| 다루지 않는 것 | 인증/인가 오류(ch30), 오류 메시지 다국어, ProblemDetail(RFC 9457) 표준 응답 |

## 다음 챕터

ch08. DataSource와 H2
