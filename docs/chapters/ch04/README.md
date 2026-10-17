# ch04. HTTP와 REST, Controller

## 학습 목표

- HTTP 메서드의 의미와 REST의 기본 원칙을 설명한다.
- `@RestController`와 `@GetMapping`으로 조회 API를 만든다.
- `@RequestParam`과 `@PathVariable`로 요청 값을 받는다.
- `ResponseEntity`로 상태 코드를 직접 정한다.
- 지원하지 않는 메서드와 잘못된 값에 대한 기본 응답(405, 400)을 확인한다.

## 선수학습

ch01, ch03

## 범위

| 항목 | 내용 |
|---|---|
| 예제 | [`examples/ch04-rest-controller`](../../../examples/ch04-rest-controller) |
| API | `GET /users`, `GET /users/{id}` |
| 테스트 | 조회 API의 정상, 404, 400, 405 (9건) |
| 다루지 않는 것 | 생성/수정/삭제(ch05), 검증(ch06), 예외 처리(ch07), 데이터베이스(ch08 이후) |

## 다음 챕터

ch05. CRUD API와 DTO
