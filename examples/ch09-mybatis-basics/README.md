# ch09. MyBatis 기본과 CRUD

Mapper 인터페이스와 XML Mapper가 이어지는 방식을 설명한다.
설명은 [docs/chapters/ch09](../../docs/chapters/ch09/README.md)에 있다.

## 환경

- JDK 21
- Gradle Wrapper 포함 (별도 설치 불필요)

## 범위

| 항목 | 내용 |
|---|---|
| API | `/users` CRUD (H2) |
| 테스트 | Mapper 단위 테스트와 API 테스트 (15건) |
| 다루지 않는 것 | 동적 SQL과 JOIN(ch10), 트랜잭션(ch11) |

## 구조

```text
ch09-mybatis-basics/
├── build.gradle
└── src/
    ├── main/
    │   ├── java/com/jqdom/curriculum/ch09/
    │   │   ├── Ch09Application.java
    │   │   ├── User.java
    │   │   ├── UserController.java
    │   │   ├── UserMapper.java
    │   │   ├── UserRequest.java
    │   │   ├── UserResponse.java
    │   │   └── UserService.java
    │   └── resources/
    │       ├── application.yml
    │       ├── data.sql
    │       ├── mapper/
    │       │   └── UserMapper.xml
    │       └── schema.sql
    └── test/java/com/jqdom/curriculum/ch09/
        ├── Ch09ApplicationTests.java
        ├── UserApiTests.java
        └── UserMapperTests.java
```

## 실행 (저장소 루트에서)

| OS | 명령 |
|---|---|
| Linux / macOS | `./gradlew :examples:ch09-mybatis-basics:bootRun` |
| Windows | `gradlew.bat :examples:ch09-mybatis-basics:bootRun` |

## 빌드와 테스트

| OS | 이 챕터만 | 전체 |
|---|---|---|
| Linux / macOS | `./gradlew :examples:ch09-mybatis-basics:build` | `./gradlew clean build` |
| Windows | `gradlew.bat :examples:ch09-mybatis-basics:build` | `gradlew.bat clean build` |
