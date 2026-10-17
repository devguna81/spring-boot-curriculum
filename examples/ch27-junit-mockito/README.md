# ch27. JUnit과 Mockito

JUnit Jupiter의 `@Test`, `@Nested`, `@DisplayName`, `@ParameterizedTest`로 테스트를 읽기 쉽게 구성한다.
설명은 [docs/chapters/ch27](../../docs/chapters/ch27/README.md)에 있다.

## 환경

- JDK 21
- Gradle Wrapper 포함 (별도 설치 불필요)

## 범위

| 항목 | 내용 |
|---|---|
| 대상 | `SignupService`(이메일 정규화, 중복 거절, 저장 후 메일 발송), `DiscountPolicy`(가입 기간별 할인율) |
| 테스트 | `SignupServiceTests` 6건, `DiscountPolicyTests` 7건(매개변수 6건 포함) (13건) |
| 특징 | 스프링 컨테이너를 쓰지 않는다. 실행 가능한 애플리케이션이 아니라서 `bootJar`는 끈다 |
| 다루지 않는 것 | 정적 메서드 목, PowerMock, 변경 테스트(mutation testing) 도구 |

## 구조

```text
ch27-junit-mockito/
├── build.gradle
└── src/
    ├── main/java/com/jqdom/curriculum/ch27/
    │   ├── DiscountPolicy.java
    │   ├── DuplicateEmailException.java
    │   ├── MailSender.java
    │   ├── SignupService.java
    │   ├── User.java
    │   └── UserRepository.java
    └── test/java/com/jqdom/curriculum/ch27/
        ├── DiscountPolicyTests.java
        └── SignupServiceTests.java
```

## 빌드와 테스트

| OS | 이 챕터만 | 전체 |
|---|---|---|
| Linux / macOS | `./gradlew :examples:ch27-junit-mockito:build` | `./gradlew clean build` |
| Windows | `gradlew.bat :examples:ch27-junit-mockito:build` | `gradlew.bat clean build` |
