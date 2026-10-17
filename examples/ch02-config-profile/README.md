# ch02. 설정과 Profile

`application.yml`의 구조와 설정 파일 위치를 설명한다.
설명은 [docs/chapters/ch02](../../docs/chapters/ch02/README.md)에 있다.

## 환경

- JDK 21
- Gradle Wrapper 포함 (별도 설치 불필요)

## 범위

| 항목 | 내용 |
|---|---|
| API | `GET /greeting`, `GET /config` |
| 설정 파일 | `application.yml`, `application-dev.yml`, `application-prod.yml` |
| 테스트 | 기본 설정, dev, prod, 속성 덮어쓰기 (6건) |
| 다루지 않는 것 | 외부 설정 서버, 암호화된 설정, Bean 등록 방식(ch03) |

## 구조

```text
ch02-config-profile/
├── build.gradle
└── src/
    ├── main/
    │   ├── java/com/jqdom/curriculum/ch02/
    │   │   ├── Ch02Application.java
    │   │   ├── ConfigController.java
    │   │   └── GreetingProperties.java
    │   └── resources/
    │       ├── application-dev.yml
    │       ├── application-prod.yml
    │       └── application.yml
    └── test/java/com/jqdom/curriculum/ch02/
        ├── Ch02ApplicationTests.java
        ├── DefaultProfileTests.java
        ├── ProfileTests.java
        └── PropertyOverrideTests.java
```

## 실행 (저장소 루트에서)

| OS | 명령 |
|---|---|
| Linux / macOS | `./gradlew :examples:ch02-config-profile:bootRun` |
| Windows | `gradlew.bat :examples:ch02-config-profile:bootRun` |

## 빌드와 테스트

| OS | 이 챕터만 | 전체 |
|---|---|---|
| Linux / macOS | `./gradlew :examples:ch02-config-profile:build` | `./gradlew clean build` |
| Windows | `gradlew.bat :examples:ch02-config-profile:build` | `gradlew.bat clean build` |
