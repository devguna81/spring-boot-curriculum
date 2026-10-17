# ch32. Docker

멀티 스테이지 Dockerfile로 빌드 도구(JDK, Gradle)는 이미지에 남기지 않고 JRE만 담는다.
설명은 [docs/chapters/ch32](../../docs/chapters/ch32/README.md)에 있다.

## 환경

- JDK 21
- Gradle Wrapper 포함 (별도 설치 불필요)

## 범위

| 항목 | 내용 |
|---|---|
| 애플리케이션 | `GET /hello`, `GET /visits`(DB의 방문 수 증가), `/actuator/health` |
| 테스트 | Gradle 테스트 3건(H2). 컨테이너 동작은 아래 명령으로 확인 |
| 다루지 않는 것 | 이미지 레지스트리 푸시, 쿠버네티스, 이미지 취약점 스캔, 멀티 아키텍처 빌드 |

## 구조

```text
ch32-docker/
├── Dockerfile
├── build.gradle
├── docker-compose.yml
└── src/
    ├── main/
    │   ├── java/com/jqdom/curriculum/ch32/
    │   │   ├── Ch32Application.java
    │   │   └── VisitController.java
    │   └── resources/
    │       ├── application.yml
    │       └── schema.sql
    └── test/java/com/jqdom/curriculum/ch32/
        └── Ch32ApplicationTests.java
```

## 실행 (저장소 루트에서)

| OS | 명령 |
|---|---|
| Linux / macOS | `./gradlew :examples:ch32-docker:bootRun` |
| Windows | `gradlew.bat :examples:ch32-docker:bootRun` |

## 빌드와 테스트

| OS | 이 챕터만 | 전체 |
|---|---|---|
| Linux / macOS | `./gradlew :examples:ch32-docker:build` | `./gradlew clean build` |
| Windows | `gradlew.bat :examples:ch32-docker:build` | `gradlew.bat clean build` |
