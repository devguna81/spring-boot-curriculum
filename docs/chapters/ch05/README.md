# ch05. CRUD API와 DTO

## 학습 목표

- POST, GET, PUT, PATCH, DELETE가 CRUD의 어떤 동작에 대응하는지 설명한다.
- `@RequestBody`로 JSON 요청 본문을 Java record로 받는다.
- 요청 DTO, 응답 DTO, 내부 객체를 구분한다.
- `ResponseEntity`로 201 Created와 Location 헤더, 204 No Content, 404를 돌려준다.
- Controller, Service, Repository 계층의 책임을 나눈다.
- PUT의 멱등성과 PATCH의 부분 수정 방식을 확인한다.

## 선수학습

ch04

## 범위

| 항목 | 내용 |
|---|---|
| 예제 | [`examples/ch05-crud-api`](../../../examples/ch05-crud-api) |
| API | `POST/GET/PUT/PATCH/DELETE /users` |
| 테스트 | CRUD 흐름, 멱등성, 404, 415, 400 (12건) |
| 다루지 않는 것 | 입력 검증(ch06), 예외 처리 통일(ch07), 데이터베이스(ch08 이후) |

## 다음 챕터

ch06. Validation
