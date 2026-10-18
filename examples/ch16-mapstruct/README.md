# ch16. DTO, Domain, MapStruct

DTO와 Domain을 나누는 이유를 설명한다.
설명은 [docs/chapters/ch16](../../docs/chapters/ch16/README.md)에 있다.

## 환경

- JDK 21
- Gradle Wrapper 포함 (별도 설치 불필요)

## 범위

| 항목 | 내용 |
|---|---|
| API | `POST /members`, `GET /members`, `GET /members/{id}`, `PATCH /members/{id}` |
| 테스트 | 매퍼 단위 테스트, 응답에 비밀값이 없는지 API 테스트 (13건) |
| 다루지 않는 것 | 여러 소스 객체 합치기, 사용자 정의 애노테이션 |

## 구조

```text
ch16-mapstruct/
├── build.gradle
└── src/
    ├── main/
    │   ├── java/com/jqdom/curriculum/ch16/
    │   │   ├── Ch16Application.java
    │   │   ├── ClockConfig.java
    │   │   ├── CreateMemberRequest.java
    │   │   ├── Member.java
    │   │   ├── MemberController.java
    │   │   ├── MemberMapper.java
    │   │   ├── MemberResponse.java
    │   │   ├── MemberService.java
    │   │   ├── MemberStatus.java
    │   │   ├── PasswordHasher.java
    │   │   └── PatchMemberRequest.java
    │   └── resources/
    │       └── application.yml
    └── test/java/com/jqdom/curriculum/ch16/
        ├── Ch16ApplicationTests.java
        ├── MemberApiTests.java
        └── MemberMapperTests.java
```

## 실행 (저장소 루트에서)

| OS | 명령 |
|---|---|
| Linux / macOS | `./gradlew :examples:ch16-mapstruct:bootRun` |
| Windows | `gradlew.bat :examples:ch16-mapstruct:bootRun` |

## 빌드와 테스트

| OS | 이 챕터만 | 전체 |
|---|---|---|
| Linux / macOS | `./gradlew :examples:ch16-mapstruct:build` | `./gradlew clean build` |
| Windows | `gradlew.bat :examples:ch16-mapstruct:build` | `gradlew.bat clean build` |
