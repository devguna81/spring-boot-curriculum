# ch36. 모듈 호출 오케스트레이션과 온디맨드 배치

가변 길이 값 목록(회원 id)을 Controller에서 검증하고, 타입이 있는 공통 맥락(`CallContext`)으로 모듈 호출 흐름에 넘긴다.
설명은 [docs/chapters/ch36](../../docs/chapters/ch36/README.md)에 있다.

## 환경

- JDK 21
- Gradle Wrapper 포함 (별도 설치 불필요)

## 범위

| 항목 | 내용 |
|---|---|
| API | `POST /api/members/summaries`, `POST /api/batch/member-summaries`(202), `GET /api/batch/member-summaries/{jobId}` |
| 모듈 | `member`(등급 Composite), `reward`(포인트 Composite), `order`(주문 건수). 각 모듈은 `api`와 `internal`로 나뉨 |
| 호출 규칙 | 코드로 정의: `member`(STOP) -> `reward`(CONTINUE) -> `order`(WARNING) |
| 저장소 | H2 인메모리. 표는 모듈과 배치가 하나씩 소유 |
| 테스트 | 구조 4건, 오케스트레이터 9건, 모듈 api 7건, 규칙 2건, 묶음 5건, 요약 API 6건, 실패 정책 API 1건, 배치 7건, 트랜잭션 2건, 컨텍스트 1건 (44건) |
| 다루지 않는 것 | 호출 규칙을 DB 표로 관리하는 방법(`CallRuleSource` 구현만 바꾸는 확장 지점만 둠), 규칙 캐싱과 무효화, 여러 서버에서의 중복 실행 방지, 재시작 가능한 배치 프레임워크(Spring Batch) |

## 구조

```text
ch36-bulk-orchestration/
├── build.gradle
└── src/
    ├── main/
    │   ├── java/com/jqdom/curriculum/ch36/
    │   │   ├── Ch36Application.java
    │   │   ├── batch/
    │   │   │   ├── BatchAlreadyRunningException.java
    │   │   │   ├── BatchConfig.java
    │   │   │   ├── BatchController.java
    │   │   │   ├── BatchJob.java
    │   │   │   ├── BatchJobNotFoundException.java
    │   │   │   ├── BatchJobRepository.java
    │   │   │   ├── CallerRunsExecutorService.java
    │   │   │   └── SummaryBatchRunner.java
    │   │   ├── common/
    │   │   │   └── Chunks.java
    │   │   ├── member/
    │   │   │   ├── api/
    │   │   │   │   ├── MemberApi.java
    │   │   │   │   └── MemberGrade.java
    │   │   │   └── internal/
    │   │   │       ├── DormantRule.java
    │   │   │       ├── FirstMatchGradeRule.java
    │   │   │       ├── GradeRule.java
    │   │   │       ├── MemberDirectory.java
    │   │   │       ├── MemberMapper.java
    │   │   │       ├── MemberRow.java
    │   │   │       └── SeniorRule.java
    │   │   ├── orchestration/
    │   │   │   ├── CallContext.java
    │   │   │   ├── CallFailedException.java
    │   │   │   ├── CallHandler.java
    │   │   │   ├── CallOutcome.java
    │   │   │   ├── CallRule.java
    │   │   │   ├── CallRuleSource.java
    │   │   │   ├── CodeCallRules.java
    │   │   │   ├── FailPolicy.java
    │   │   │   ├── HandlerRegistry.java
    │   │   │   ├── MemberSummary.java
    │   │   │   ├── SummaryDraft.java
    │   │   │   ├── SummaryOrchestrator.java
    │   │   │   ├── SummaryResult.java
    │   │   │   └── handler/
    │   │   │       ├── MemberHandler.java
    │   │   │       ├── OrderHandler.java
    │   │   │       └── RewardHandler.java
    │   │   ├── order/
    │   │   │   ├── api/
    │   │   │   │   └── OrderApi.java
    │   │   │   └── internal/
    │   │   │       ├── OrderCountRow.java
    │   │   │       ├── OrderMapper.java
    │   │   │       └── OrderService.java
    │   │   ├── reward/
    │   │   │   ├── api/
    │   │   │   │   └── RewardApi.java
    │   │   │   └── internal/
    │   │   │       ├── EarnRule.java
    │   │   │       ├── LedgerRow.java
    │   │   │       ├── PointRule.java
    │   │   │       ├── RewardMapper.java
    │   │   │       ├── RewardService.java
    │   │   │       ├── SumPointRule.java
    │   │   │       └── UseRule.java
    │   │   └── web/
    │   │       ├── ApiExceptionHandler.java
    │   │       ├── MemberSummaryController.java
    │   │       └── SummaryRequest.java
    │   └── resources/
    │       ├── application.yml
    │       ├── data.sql
    │       ├── mapper/
    │       │   ├── MemberMapper.xml
    │       │   ├── OrderMapper.xml
    │       │   └── RewardMapper.xml
    │       └── schema.sql
    └── test/java/com/jqdom/curriculum/ch36/
        ├── ArchitectureTests.java
        ├── AsyncBatchTests.java
        ├── BatchChunkFailureTests.java
        ├── BatchTests.java
        ├── Ch36ApplicationTests.java
        ├── ChunksTests.java
        ├── FailurePolicyApiTests.java
        ├── ModuleApiTests.java
        ├── ModuleRuleTests.java
        ├── OrchestratorTests.java
        ├── SummaryApiTests.java
        ├── TransactionPolicyTests.java
        ├── member/internal/
        │   └── MemberDirectoryTestAccess.java
        ├── reward/internal/
        │   └── RewardRuleTestAccess.java
        └── support/
            ├── FailingRewardForMemberThree.java
            └── FlakyModules.java
```

## 실행 (저장소 루트에서)

| OS | 명령 |
|---|---|
| Linux / macOS | `./gradlew :examples:ch36-bulk-orchestration:bootRun` |
| Windows | `gradlew.bat :examples:ch36-bulk-orchestration:bootRun` |

## 빌드와 테스트

| OS | 이 챕터만 | 전체 |
|---|---|---|
| Linux / macOS | `./gradlew :examples:ch36-bulk-orchestration:build` | `./gradlew clean build` |
| Windows | `gradlew.bat :examples:ch36-bulk-orchestration:build` | `gradlew.bat clean build` |
