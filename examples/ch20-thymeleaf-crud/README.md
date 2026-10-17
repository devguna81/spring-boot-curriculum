# ch20. Thymeleaf CRUD

폼 객체와 `th:object`, `th:field`로 입력 화면을 만든다.
설명은 [docs/chapters/ch20](../../docs/chapters/ch20/README.md)에 있다.

## 환경

- JDK 21
- Gradle Wrapper 포함 (별도 설치 불필요)

## 범위

| 항목 | 내용 |
|---|---|
| 화면 | `/users`, `/users/new`, `/users/{id}/edit`, 수정과 삭제는 POST |
| 테스트 | 폼 표시, 검증 실패, 변환 오류, 중복 이메일, PRG와 Flash, 이스케이프, 수정, 삭제, 404 (14건) |
| 다루지 않는 것 | CSRF 방어와 인증(ch30), DB 저장(메모리 저장소 사용), 파일 업로드 |

## 구조

```text
ch20-thymeleaf-crud/
├── build.gradle
└── src/
    ├── main/
    │   ├── java/com/jqdom/curriculum/ch20/
    │   │   ├── Ch20Application.java
    │   │   ├── DuplicateEmailException.java
    │   │   ├── User.java
    │   │   ├── UserForm.java
    │   │   ├── UserNotFoundException.java
    │   │   ├── UserPageController.java
    │   │   └── UserService.java
    │   └── resources/
    │       ├── application.yml
    │       ├── messages.properties
    │       └── templates/
    │           ├── error/
    │           │   └── 404.html
    │           ├── fragments/
    │           │   └── layout.html
    │           └── users/
    │               ├── form.html
    │               └── list.html
    └── test/java/com/jqdom/curriculum/ch20/
        └── UserPageTests.java
```

## 실행 (저장소 루트에서)

| OS | 명령 |
|---|---|
| Linux / macOS | `./gradlew :examples:ch20-thymeleaf-crud:bootRun` |
| Windows | `gradlew.bat :examples:ch20-thymeleaf-crud:bootRun` |

## 빌드와 테스트

| OS | 이 챕터만 | 전체 |
|---|---|---|
| Linux / macOS | `./gradlew :examples:ch20-thymeleaf-crud:build` | `./gradlew clean build` |
| Windows | `gradlew.bat :examples:ch20-thymeleaf-crud:build` | `gradlew.bat clean build` |
