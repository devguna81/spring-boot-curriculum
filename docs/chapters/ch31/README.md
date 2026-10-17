# ch31. Logging과 Actuator

## 학습 목표

- 로그 레벨을 패키지별로 정하고, 로그 패턴에 요청 번호(MDC)를 넣는다.
- 필터에서 요청마다 번호를 정해 MDC에 넣고, 요청이 끝나면 반드시 지운다.
- 같은 로그를 JSON 한 줄로 내보내는 구조화 로그 설정을 확인한다.
- Actuator의 `health`, `info`, `metrics`, `loggers`를 열고, 열지 않은 엔드포인트가 막혀 있음을 확인한다.
- 커스텀 `HealthIndicator`와 Micrometer 카운터를 만든다.

## 선수학습

ch02

## 범위

| 항목 | 내용 |
|---|---|
| 예제 | [`examples/ch31-logging-actuator`](../../../examples/ch31-logging-actuator) |
| 노출 엔드포인트 | `health`, `info`, `metrics`, `loggers` (그 외는 404) |
| 테스트 | Actuator 8건, 로깅 5건, 구조화 로그 1건, 컨텍스트 1건 (15건) |
| 다루지 않는 것 | 로그 수집 시스템(ELK 등), Prometheus 연동, 분산 추적, 엔드포인트 인증 |

## 확인한 것

- 구성 요소 하나(`stock`)가 DOWN이면 `/actuator/health` 전체가 DOWN이고 HTTP 503이다.
- 요청 번호 헤더 값에서 줄바꿈 등 문자를 제거하지 않으면 로그에 가짜 줄을 넣을 수 있다. 정제한 값만 로그에 나타나는 것을 테스트로 확인했다.
- 필터에서 `MDC.remove`를 빼면 요청이 끝난 뒤의 로그에도 이전 요청 번호가 남는다. `mdcIsClearedAfterTheRequest`가 이를 잡는다.
- `POST /actuator/loggers/{name}`으로 서버를 재시작하지 않고 로그 레벨을 바꿀 수 있다.

## 주의

`health`의 `show-details: always`는 학습 확인용이다. 외부에 노출되는 서버에서는 세부 정보를 제한한다. `/actuator/loggers`의 쓰기 기능도 인증 없이 열어 두면 안 된다(예제에는 인증을 붙이지 않았다).

## 실행

```bash
./gradlew :examples:ch31-logging-actuator:test
./gradlew :examples:ch31-logging-actuator:bootRun
# curl http://localhost:8080/actuator/health
```

## 다음 챕터

ch32. Docker
