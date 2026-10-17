# ch19. Thymeleaf 기초

`@Controller`가 반환한 문자열이 뷰 이름으로 해석되고, `Model`의 값이 템플릿으로 전달되는 흐름을 이해한다.
설명은 [docs/chapters/ch19](../../docs/chapters/ch19/README.md)에 있다.

## 환경

- JDK 21
- Gradle Wrapper 포함 (별도 설치 불필요)

## 범위

| 항목 | 내용 |
|---|---|
| 화면 | `/users` 목록(활성 필터), `/users/{id}` 상세, 404 페이지 |
| 테스트 | 뷰 이름, 모델, 렌더링된 HTML, 이스케이프 (10건) |
| 다루지 않는 것 | 폼 입력과 저장(ch20), 정적 리소스 번들링, 다국어 메시지 |

## 구조

```text
ch19-thymeleaf/
├── build.gradle
└── src/
    ├── main/
    │   ├── java/com/devguna/curriculum/ch19/
    │   │   ├── Ch19Application.java
    │   │   ├── User.java
    │   │   ├── UserNotFoundException.java
    │   │   ├── UserPageController.java
    │   │   └── UserRepository.java
    │   └── resources/
    │       ├── application.yml
    │       └── templates/
    │           ├── error/
    │           │   └── 404.html
    │           ├── fragments/
    │           │   └── layout.html
    │           └── users/
    │               ├── detail.html
    │               └── list.html
    └── test/java/com/devguna/curriculum/ch19/
        └── UserPageTests.java
```

## 실행 (저장소 루트에서)

| OS | 명령 |
|---|---|
| Linux / macOS | `./gradlew :examples:ch19-thymeleaf:bootRun` |
| Windows | `gradlew.bat :examples:ch19-thymeleaf:bootRun` |

## 빌드와 테스트

| OS | 이 챕터만 | 전체 |
|---|---|---|
| Linux / macOS | `./gradlew :examples:ch19-thymeleaf:build` | `./gradlew clean build` |
| Windows | `gradlew.bat :examples:ch19-thymeleaf:build` | `gradlew.bat clean build` |
