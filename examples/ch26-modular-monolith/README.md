# ch26. Modular Monolith

하나의 애플리케이션 안에서 `member`, `catalog`, `order`, `reward` 모듈을 나누고, 각 모듈은 `api`(공개)와 `internal`(비공개) 패키지를 가진다.
설명은 [docs/chapters/ch26](../../docs/chapters/ch26/README.md)에 있다.

## 환경

- JDK 21
- Gradle Wrapper 포함 (별도 설치 불필요)

## 범위

| 항목 | 내용 |
|---|---|
| API | `POST /api/orders` (회원 id, 상품 코드, 수량. 없는 회원/상품 404, 입력 오류 400) |
| 모듈 의존 | `order` -> `member.api`, `catalog.api` / `reward` -> `order.api` (순환 없음) |
| 저장소 | H2 인메모리. 테이블은 모듈이 하나씩 소유한다(`member`, `orders`, `reward`) |
| 테스트 | 구조 8건, 주문 흐름 5건, 트랜잭션 경계 3건, 컨텍스트 1건 (17건) |
| 다루지 않는 것 | Spring Modulith 같은 모듈 전용 라이브러리, 모듈별 Gradle 서브프로젝트 분리, 이벤트 영속화와 재전송 |

## 구조

```text
ch26-modular-monolith/
├── build.gradle
└── src/
    ├── main/
    │   ├── java/com/devguna/curriculum/ch26/
    │   │   ├── Ch26Application.java
    │   │   ├── catalog/
    │   │   │   ├── api/
    │   │   │   │   └── CatalogApi.java
    │   │   │   └── internal/
    │   │   │       └── InMemoryCatalog.java
    │   │   ├── member/
    │   │   │   ├── api/
    │   │   │   │   └── MemberApi.java
    │   │   │   └── internal/
    │   │   │       └── JdbcMemberDirectory.java
    │   │   ├── order/
    │   │   │   ├── api/
    │   │   │   │   ├── OrderApi.java
    │   │   │   │   ├── OrderPlaced.java
    │   │   │   │   ├── UnknownMemberException.java
    │   │   │   │   └── UnknownProductException.java
    │   │   │   └── internal/
    │   │   │       ├── OrderController.java
    │   │   │       └── OrderService.java
    │   │   └── reward/
    │   │       ├── api/
    │   │       │   └── RewardApi.java
    │   │       └── internal/
    │   │           └── RewardService.java
    │   └── resources/
    │       ├── application.yml
    │       ├── data.sql
    │       └── schema.sql
    └── test/java/com/devguna/curriculum/ch26/
        ├── ArchitectureTests.java
        ├── Ch26ApplicationTests.java
        ├── OrderFlowTests.java
        ├── TransactionBoundaryTests.java
        └── fixture/
            ├── cycle/
            │   ├── a/
            │   │   └── ClassA.java
            │   └── b/
            │       └── ClassB.java
            └── exposed/internal/
                └── PublicHelper.java
```

## 실행 (저장소 루트에서)

| OS | 명령 |
|---|---|
| Linux / macOS | `./gradlew :examples:ch26-modular-monolith:bootRun` |
| Windows | `gradlew.bat :examples:ch26-modular-monolith:bootRun` |

## 빌드와 테스트

| OS | 이 챕터만 | 전체 |
|---|---|---|
| Linux / macOS | `./gradlew :examples:ch26-modular-monolith:build` | `./gradlew clean build` |
| Windows | `gradlew.bat :examples:ch26-modular-monolith:build` | `gradlew.bat clean build` |
