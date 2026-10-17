# ch02. 설정과 Profile

## 학습 목표

- `application.yml`의 구조와 설정 파일 위치를 설명한다.
- `@Value`와 `@ConfigurationProperties`로 설정을 읽는다.
- Profile로 개발/운영 환경의 설정을 나눈다.
- 명령행 인자와 환경 변수로 설정을 덮어쓰고, 우선순위를 확인한다.

## 선수학습

ch01. Hello Spring Boot

## 범위

| 항목 | 내용 |
|---|---|
| 예제 | [`examples/ch02-config-profile`](../../../examples/ch02-config-profile) |
| API | `GET /greeting`, `GET /config` |
| 설정 파일 | `application.yml`, `application-dev.yml`, `application-prod.yml` |
| 테스트 | 기본 설정, dev, prod, 속성 덮어쓰기 (6건) |
| 다루지 않는 것 | 외부 설정 서버, 암호화된 설정, Bean 등록 방식(ch03) |

## 설정 우선순위 (이 예제에서 확인하는 범위)

아래로 갈수록 우선순위가 높다.

1. `application.yml` (기본)
2. `application-{profile}.yml` (활성 Profile)
3. 환경 변수 (`APP_GREETING_PREFIX`)
4. 명령행 인자 (`--app.greeting.prefix=...`)

전체 순서는 Spring Boot 공식 문서의 Externalized Configuration 항목에 있다.

## 다음 챕터

ch03. IoC, DI, Bean
