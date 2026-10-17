# f01. 설계

## 학습 목표

- 요구사항을 기능, 비기능, 범위 밖으로 나눠 적는다.
- 모듈 구조와 의존 방향을 코드를 쓰기 전에 정하고, 구조 테스트의 첫 규칙으로 고정한다.
- 테이블을 설계하고 H2와 Oracle의 차이를 스크립트로 분리한다.
- API 계약을 `contract` 패키지의 타입과 검증 규칙으로 정의하고 테스트한다.

## 선수학습

ch18, ch26

## 범위

| 항목 | 내용 |
|---|---|
| 프로젝트 | [`projects/final-project`](../../../projects/final-project) |
| 설계 문서 | [`projects/final-project/docs`](../../../projects/final-project/docs) |
| 코드 | 스키마(H2, Oracle), `contract` 타입, 구조 규칙 |
| 테스트 | 스키마 5건, 계약 검증 4건, 구조 2건, 컨텍스트 1건, Oracle 스키마 2건(`ORACLE_URL`이 있을 때만) |
| 다루지 않는 것 | 컨트롤러와 서비스(f02) |

## 실행

```bash
./gradlew :projects:final-project:test
```

## 다음 챕터

f02. 백엔드
