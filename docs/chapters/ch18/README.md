# ch18. API Contract

## 학습 목표

- 외부 계약(contract)과 내부 객체(internal)를 패키지로 분리한다.
- 경로의 버전(/api/v1)으로 계약 버전을 표현한다.
- OpenAPI 문서를 저장본과 비교해 계약 변경을 감지한다.
- 호환되는 변경과 깨지는 변경을 구분해 테스트로 확인한다.
- ArchUnit으로 패키지 의존 방향을 검사한다.

## 선수학습

ch16, ch17

## 범위

| 항목 | 내용 |
|---|---|
| 예제 | [`examples/ch18-api-contract`](../../../examples/ch18-api-contract) |
| API | `/api/v1/users` (생성, 목록, 단건) |
| 테스트 | API 테스트, 계약 저장본 비교, 호환성 판정, 의존 방향 (24건) |
| 다루지 않는 것 | 소비자 주도 계약 테스트(Pact), 버전 병행 운영 |

## 다음 챕터

ch19. Thymeleaf 기초
