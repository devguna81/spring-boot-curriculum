# ch06. Validation

## 학습 목표

- Jakarta Validation의 제약 애노테이션으로 요청 DTO의 규칙을 선언한다.
- `@Valid`로 요청 본문을, 메서드 인자 제약으로 경로 변수와 쿼리 파라미터를 검증한다.
- PUT, PATCH에서 검증 방식이 어떻게 달라지는지 구분한다.
- 직접 제약 애노테이션과 검증기를 만든다.
- Spring 없이 `Validator`만으로 제약을 테스트한다.

## 선수학습

ch05

## 범위

| 항목 | 내용 |
|---|---|
| 예제 | [`examples/ch06-validation`](../../../examples/ch06-validation) |
| API | `POST/PUT/PATCH /users` 요청 검증, `GET /users?limit=`, `GET /users/{id}` 인자 검증 |
| 테스트 | 제약 단위 테스트, 검증 실패 400 (15건) |
| 다루지 않는 것 | 오류 응답 형식 통일(ch07), 그룹 검증과 교차 필드 검증 |

## 다음 챕터

ch07. 예외 처리와 공통 응답
