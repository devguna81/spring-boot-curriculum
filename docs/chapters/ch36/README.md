# ch36. 모듈 호출 오케스트레이션과 온디맨드 배치

## 학습 목표

- 가변 길이 값 목록(회원 id)을 Controller에서 검증하고, 타입이 있는 공통 맥락(`CallContext`)으로 모듈 호출 흐름에 넘긴다.
- 호출 정책(누구를, 어떤 순서로, 실패하면 어떻게)과 업무 로직을 분리한다: 규칙, Orchestrator, Registry, Handler, 모듈 `api`.
- Handler가 공통 맥락을 모듈 `api` 호출로 바꾸고, 모듈 안에서는 Composite 규칙이 업무를 처리한다.
- 실패 정책(`STOP`, `CONTINUE`, `WARNING`, `SKIP`)에 따라 한 모듈의 실패가 전체에 미치는 영향이 달라지는 것을 확인한다.
- 값 목록이 길어도 모듈마다 한 번씩 호출되고, DB에는 묶음 단위 IN으로 나뉘어 나가는 것을 확인한다.
- 호출될 때 시작하는 온디맨드 배치를 chunk 단위로 처리하고, 재실행해도 결과가 늘어나지 않으며, 동시에 하나만 실행되게 한다.

## 선수학습

ch10, ch11, ch24, ch25, ch26

## 범위

| 항목 | 내용 |
|---|---|
| 예제 | [`examples/ch36-bulk-orchestration`](../../../examples/ch36-bulk-orchestration) |
| API | `POST /api/members/summaries`, `POST /api/batch/member-summaries`(202), `GET /api/batch/member-summaries/{jobId}` |
| 모듈 | `member`(등급 Composite), `reward`(포인트 Composite), `order`(주문 건수). 각 모듈은 `api`와 `internal`로 나뉨 |
| 호출 규칙 | 코드로 정의: `member`(STOP) -> `reward`(CONTINUE) -> `order`(WARNING) |
| 저장소 | H2 인메모리. 표는 모듈과 배치가 하나씩 소유 |
| 테스트 | 구조 4건, 오케스트레이터 9건, 모듈 api 7건, 규칙 2건, 묶음 5건, 요약 API 6건, 실패 정책 API 1건, 배치 7건, 트랜잭션 2건, 컨텍스트 1건 (44건) |
| 다루지 않는 것 | 호출 규칙을 DB 표로 관리하는 방법(`CallRuleSource` 구현만 바꾸는 확장 지점만 둠), 규칙 캐싱과 무효화, 여러 서버에서의 중복 실행 방지, 재시작 가능한 배치 프레임워크(Spring Batch) |

## 흐름

```text
Controller: 값 목록 검증(비어 있음, 1000개 초과, null, 양수 아님)
  -> CallContext(List<Long>): 중복 제거, 불변
  -> SummaryOrchestrator: 규칙을 execSeq 순서로 실행, 실패 정책 적용
       -> HandlerRegistry: key("member", "reward", "order")로 Handler 조회
            -> Handler: member.api / reward.api / order.api를 목록 한 번으로 호출
                 -> 모듈 안: IN을 묶음으로 나누어 조회, Composite 규칙이 등급/포인트 계산
```

## 가변 IN 처리 원칙

- 값 목록은 `List<Long>` 같은 타입으로 전달하고 `Map`이나 JSON 노드로 만들지 않는다.
- 호출 쪽은 모듈마다 한 번만 부른다(id마다 호출하지 않는다).
- 목록 길이 제한은 입구(Controller)에서 정하고, DB로 나가는 IN 길이는 모듈 안에서 설정(`bulk.in-chunk-size`)으로 나눈다.
- 모듈 계약에 적는다: 빈 목록은 빈 결과, 중복은 한 번만, 없는 id는 키 없음.

## 확인하지 않은 것

- IN 목록 길이 제한 값은 DB마다 다르며 이 예제는 H2에서만 확인했습니다. 설정 값 500은 예시입니다.
- `MERGE ... KEY`는 H2 문법입니다. 다른 DB에서는 `MERGE`/`ON CONFLICT` 문법이 달라 SQL을 바꿔야 합니다.
- 배치 중복 실행 방지는 서버 한 대 기준(`synchronized` + 상태 표 조회)이며, 여러 서버에서는 DB 잠금 등이 필요합니다.
- 배치 도중 서버가 종료되면 `RUNNING`으로 남는 작업의 정리는 구현하지 않았습니다.
- 실패 정책과 트랜잭션 경계는 테스트로 확인한 범위(조회 파이프라인, 안쪽 `REQUIRES_NEW`)만 다룹니다.

## 실행과 테스트

```bash
./gradlew :examples:ch36-bulk-orchestration:test
./gradlew :examples:ch36-bulk-orchestration:bootRun
```

```bash
curl -X POST localhost:8080/api/members/summaries -H 'Content-Type: application/json' -d '{"memberIds":[1,2,3,99]}'
curl -X POST localhost:8080/api/batch/member-summaries
```

## 다음 챕터

f01. 종합 프로젝트 설계
