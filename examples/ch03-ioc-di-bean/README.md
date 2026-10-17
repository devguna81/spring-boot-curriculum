# ch03. IoC, DI, Bean

IoC와 DI가 무엇이고 왜 쓰는지 설명한다.
설명은 [docs/chapters/ch03](../../docs/chapters/ch03/README.md)에 있다.

## 환경

- JDK 21
- Gradle Wrapper 포함 (별도 설치 불필요)

## 범위

| 항목 | 내용 |
|---|---|
| API | `GET /orders`, `GET /senders`, `GET /beans` |
| 테스트 | 생성자 주입 단위 테스트, Bean 연결, Scope와 생명주기 (12건) |
| 다루지 않는 것 | Bean 후처리기, `@Conditional`, AOP |

## 구조

```text
ch03-ioc-di-bean/
├── build.gradle
└── src/
    ├── main/
    │   ├── java/com/devguna/curriculum/
    │   │   ├── ch03/
    │   │   │   ├── AppConfig.java
    │   │   │   ├── BeanController.java
    │   │   │   ├── Ch03Application.java
    │   │   │   ├── EmailSender.java
    │   │   │   ├── LifecycleRecorder.java
    │   │   │   ├── NotificationSender.java
    │   │   │   ├── OrderService.java
    │   │   │   └── SmsSender.java
    │   │   └── outside/
    │   │       └── OutsideComponent.java
    │   └── resources/
    │       └── application.yml
    └── test/java/com/devguna/curriculum/ch03/
        ├── BeanWiringTests.java
        ├── Ch03ApplicationTests.java
        ├── OrderServiceUnitTests.java
        └── ScopeAndLifecycleTests.java
```

## 실행 (저장소 루트에서)

| OS | 명령 |
|---|---|
| Linux / macOS | `./gradlew :examples:ch03-ioc-di-bean:bootRun` |
| Windows | `gradlew.bat :examples:ch03-ioc-di-bean:bootRun` |

## 빌드와 테스트

| OS | 이 챕터만 | 전체 |
|---|---|---|
| Linux / macOS | `./gradlew :examples:ch03-ioc-di-bean:build` | `./gradlew clean build` |
| Windows | `gradlew.bat :examples:ch03-ioc-di-bean:build` | `gradlew.bat clean build` |
