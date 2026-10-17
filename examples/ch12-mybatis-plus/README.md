# ch12. MyBatis-Plus로 반복 CRUD 줄이기

BaseMapper로 반복되는 CRUD SQL을 제거한다.
설명은 [docs/chapters/ch12](../../docs/chapters/ch12/README.md)에 있다.

## 환경

- JDK 21
- Gradle Wrapper 포함 (별도 설치 불필요)

## 범위

| 항목 | 내용 |
|---|---|
| API | `/users` CRUD, `GET /users/search`, `GET /users/older-than/{age}` |
| 테스트 | ch09와 같은 시나리오 + 조건 조합, 페이징 (19건) |
| 다루지 않는 것 | 코드 생성기, 논리 삭제, 자동 채움 |

## 구조

```text
ch12-mybatis-plus/
├── build.gradle
└── src/
    ├── main/
    │   ├── java/com/devguna/curriculum/ch12/
    │   │   ├── Ch12Application.java
    │   │   ├── MybatisPlusConfig.java
    │   │   ├── PageResponse.java
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
    └── test/java/com/devguna/curriculum/ch12/
        ├── Ch12ApplicationTests.java
        ├── UserApiTests.java
        └── UserMapperTests.java
```

## 실행 (저장소 루트에서)

| OS | 명령 |
|---|---|
| Linux / macOS | `./gradlew :examples:ch12-mybatis-plus:bootRun` |
| Windows | `gradlew.bat :examples:ch12-mybatis-plus:bootRun` |

## 빌드와 테스트

| OS | 이 챕터만 | 전체 |
|---|---|---|
| Linux / macOS | `./gradlew :examples:ch12-mybatis-plus:build` | `./gradlew clean build` |
| Windows | `gradlew.bat :examples:ch12-mybatis-plus:build` | `gradlew.bat clean build` |
