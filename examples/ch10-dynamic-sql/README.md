# ch10. 동적 SQL, JOIN, 페이징

<if>, <where>, <foreach>, <choose>로 검색 조건을 조합한다.
설명은 [docs/chapters/ch10](../../docs/chapters/ch10/README.md)에 있다.

## 환경

- JDK 21
- Gradle Wrapper 포함 (별도 설치 불필요)

## 범위

| 항목 | 내용 |
|---|---|
| API | `GET /posts`, `GET /users/{id}/posts` |
| 테스트 | 조건 조합, JOIN 매핑, 페이징 경계 (22건) |
| 다루지 않는 것 | N+1 문제의 해결 전략, 커서 기반 페이징 |

## 구조

```text
ch10-dynamic-sql/
├── build.gradle
└── src/
    ├── main/
    │   ├── java/com/jqdom/curriculum/ch10/
    │   │   ├── Ch10Application.java
    │   │   ├── PageResponse.java
    │   │   ├── PostController.java
    │   │   ├── PostMapper.java
    │   │   ├── PostSearch.java
    │   │   ├── PostService.java
    │   │   ├── PostSummary.java
    │   │   └── UserWithPosts.java
    │   └── resources/
    │       ├── application.yml
    │       ├── data.sql
    │       ├── mapper/
    │       │   └── PostMapper.xml
    │       └── schema.sql
    └── test/java/com/jqdom/curriculum/ch10/
        ├── Ch10ApplicationTests.java
        ├── PostApiTests.java
        └── PostMapperTests.java
```

## 실행 (저장소 루트에서)

| OS | 명령 |
|---|---|
| Linux / macOS | `./gradlew :examples:ch10-dynamic-sql:bootRun` |
| Windows | `gradlew.bat :examples:ch10-dynamic-sql:bootRun` |

## 빌드와 테스트

| OS | 이 챕터만 | 전체 |
|---|---|---|
| Linux / macOS | `./gradlew :examples:ch10-dynamic-sql:build` | `./gradlew clean build` |
| Windows | `gradlew.bat :examples:ch10-dynamic-sql:build` | `gradlew.bat clean build` |
