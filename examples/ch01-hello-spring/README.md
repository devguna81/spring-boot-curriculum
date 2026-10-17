# ch01. Hello Spring Boot

Spring Boot 프로젝트의 구조를 이해하고 첫 REST API(`GET /hello`, `GET /info`)를 실행한다.
설명은 [docs/chapters/ch01](../../docs/chapters/ch01/README.md)에 있다.

## 환경

- JDK 21
- Gradle Wrapper 포함 (별도 설치 불필요)

## 구조

```text
ch01-hello-spring/
├── build.gradle
└── src
    ├── main
    │   ├── java/com/devguna/curriculum/ch01
    │   │   ├── Ch01Application.java    # 시작점 (@SpringBootApplication)
    │   │   ├── HelloController.java    # GET /hello, GET /info
    │   │   └── AppInfo.java            # /info 응답 record
    │   └── resources/application.yml
    └── test/java/com/devguna/curriculum/ch01
        ├── Ch01ApplicationTests.java   # 컨텍스트 로딩
        └── HelloControllerTests.java   # API 응답 검증
```

## 실행 (저장소 루트에서)

| OS | 명령 |
|---|---|
| Linux / macOS | `./gradlew :examples:ch01-hello-spring:bootRun` |
| Windows | `gradlew.bat :examples:ch01-hello-spring:bootRun` |

## 호출

```bash
curl http://localhost:8080/hello
curl "http://localhost:8080/hello?name=devguna"
curl http://localhost:8080/info
```

## 빌드와 테스트

| OS | 이 챕터만 | 전체 |
|---|---|---|
| Linux / macOS | `./gradlew :examples:ch01-hello-spring:build` | `./gradlew clean build` |
| Windows | `gradlew.bat :examples:ch01-hello-spring:build` | `gradlew.bat clean build` |
