# ch30. Spring Security

인증(누구인가)과 인가(무엇을 할 수 있는가)를 구분한다.
설명은 [docs/chapters/ch30](../../docs/chapters/ch30/README.md)에 있다.

## 환경

- JDK 21
- Gradle Wrapper 포함 (별도 설치 불필요)

## 범위

| 항목 | 내용 |
|---|---|
| 방식 | HTTP Basic, 세션 없음(stateless), CSRF 토큰 끔 |
| 계정 | 학습용 메모리 계정 3개(kim, lee: USER / root: USER+ADMIN) |
| 테스트 | 인증 5건, 인가 5건, 메서드 보안 4건, 무상태 2건, 컨텍스트 1건 (16건) |
| 다루지 않는 것 | 폼 로그인, JWT, OAuth2, CORS 연동(ch22 참고), DB 계정 저장 |

## 구조

```text
ch30-security/
├── build.gradle
└── src/
    ├── main/
    │   ├── java/com/jqdom/curriculum/ch30/
    │   │   ├── ApiController.java
    │   │   ├── Ch30Application.java
    │   │   ├── Note.java
    │   │   ├── NoteService.java
    │   │   └── SecurityConfig.java
    │   └── resources/
    │       └── application.yml
    └── test/java/com/jqdom/curriculum/ch30/
        ├── Ch30ApplicationTests.java
        └── SecurityApiTests.java
```

## 실행 (저장소 루트에서)

| OS | 명령 |
|---|---|
| Linux / macOS | `./gradlew :examples:ch30-security:bootRun` |
| Windows | `gradlew.bat :examples:ch30-security:bootRun` |

## 빌드와 테스트

| OS | 이 챕터만 | 전체 |
|---|---|---|
| Linux / macOS | `./gradlew :examples:ch30-security:build` | `./gradlew clean build` |
| Windows | `gradlew.bat :examples:ch30-security:build` | `gradlew.bat clean build` |
