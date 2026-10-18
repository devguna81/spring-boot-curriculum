# ch35. AOP

실행 시간 측정처럼 여러 메서드에 반복되는 부가 기능을 Aspect로 분리한다.
설명은 [docs/chapters/ch35](../../docs/chapters/ch35/README.md)에 있다.

## 환경

- JDK 21
- Gradle Wrapper 포함 (별도 설치 불필요)

## 범위

| 항목 | 내용 |
|---|---|
| 의존성 | `spring-boot-starter-aspectj` (Boot 4.1.1에는 `spring-boot-starter-aop`가 없다) |
| API | `GET /api/greet`, `GET /api/executions` |
| Aspect | `MeasuredAspect`(`@Measured` 대상, `@Around`), `CallCounter`(service 패키지, `@Before`) |
| 테스트 | Aspect 8건, API 1건, 컨텍스트 1건 (10건) |
| 다루지 않는 것 | `@AfterReturning`/`@AfterThrowing` 세부, 클래스 로드 시점 위빙(AspectJ 에이전트), 순서 지정(`@Order`) |

## 구조

```text
ch35-aop/
├── build.gradle
└── src/
    ├── main/
    │   ├── java/com/jqdom/curriculum/ch35/
    │   │   ├── Ch35Application.java
    │   │   ├── aspect/
    │   │   │   ├── CallCounter.java
    │   │   │   ├── ExecutionLog.java
    │   │   │   ├── ExecutionRecord.java
    │   │   │   ├── Measured.java
    │   │   │   └── MeasuredAspect.java
    │   │   ├── service/
    │   │   │   ├── CallerService.java
    │   │   │   └── GreetingService.java
    │   │   └── web/
    │   │       └── AopController.java
    │   └── resources/
    │       └── application.yml
    └── test/java/com/jqdom/curriculum/ch35/
        ├── AopControllerTests.java
        ├── AspectTests.java
        └── Ch35ApplicationTests.java
```

## 실행 (저장소 루트에서)

| OS | 명령 |
|---|---|
| Linux / macOS | `./gradlew :examples:ch35-aop:bootRun` |
| Windows | `gradlew.bat :examples:ch35-aop:bootRun` |

## 빌드와 테스트

| OS | 이 챕터만 | 전체 |
|---|---|---|
| Linux / macOS | `./gradlew :examples:ch35-aop:build` | `./gradlew clean build` |
| Windows | `gradlew.bat :examples:ch35-aop:build` | `gradlew.bat clean build` |
