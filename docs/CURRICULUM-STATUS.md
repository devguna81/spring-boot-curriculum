# CURRICULUM-STATUS

| 상태 | 의미 |
|---|---|
| PLANNED | 커리큘럼 설계 완료 |
| IMPLEMENTING | 코드 구현 중 |
| CODE_READY | 코드 작성 완료 |
| VERIFIED | 빌드 및 테스트 성공 |
| DRAFT_READY | MD 초안 완료 |
| REVIEWED | 사용자 검토 및 승인 |
| PUBLISHED | Agy 게시 완료 |

최종 갱신: 2026-10-17

## ch01

| 항목 | 값 |
|---|---|
| 제목 | Hello Spring Boot |
| 상태 | REVIEWED (사용자 검토 완료, 2026-10-17) |
| 예제 코드 | `examples/ch01-hello-spring` |
| 문서 | `docs/chapters/ch01/README.md`, `blog-draft.md`, `verification.md` |
| 빌드 결과 | `./gradlew --no-daemon clean build` BUILD SUCCESSFUL (Docker, JDK 21.0.11) |
| 테스트 결과 | 4건 중 4건 통과 |
| 검증 Commit SHA | `61a3bb118238a2f9e8632802c81844f48e9018cc` (예제 코드). `main` 반영: PR #1 머지 커밋 `fabbb471c311661f17a180eed2cae656f9910c6d` |
| CI 결과 | 성공 - [run 38996455608](https://github.com/devguna81/spring-boot-curriculum/actions/runs/38996455608), 테스트 4건 통과를 로그로 확인 (PR #1, 커밋 `654d794`) |
| 블로그 게시 상태 | 미게시 |
| 블로그 게시 URL | - |

## ch02 이후

| 챕터 | 제목 | 상태 |
|---|---|---|
| ch02 | 설정과 Profile | PLANNED |
| ch03 | IoC, DI, Bean | PLANNED |
| ch04 | HTTP와 REST, Controller | PLANNED |
| ch05 | CRUD API와 DTO | PLANNED |
| ch06 | Validation | PLANNED |
| ch07 | 예외 처리와 공통 응답 | PLANNED |
| ch08 | DataSource와 H2 | PLANNED |
| ch09 | MyBatis 기본과 CRUD | PLANNED |
| ch10 | 동적 SQL, JOIN, 페이징 | PLANNED |
| ch11 | 트랜잭션 | PLANNED |
| ch12 | MyBatis-Plus로 반복 CRUD 줄이기 | PLANNED |
| ch13 | Oracle 연결 | PLANNED |
| ch14 | Oracle JSON | PLANNED |
| ch15 | JSON과 Java 객체 변환 | PLANNED |
| ch16 | DTO, Domain, MapStruct | PLANNED |
| ch17 | OpenAPI와 Swagger UI | PLANNED |
| ch18 | API Contract | PLANNED |
| ch19 | Thymeleaf 기초 | PLANNED |
| ch20 | Thymeleaf CRUD | PLANNED |
| ch21 | React 기초 | PLANNED |
| ch22 | React와 REST API 연동 | PLANNED |
| ch23 | Layered Architecture와 SOLID | PLANNED |
| ch24 | Contract와 Port/Adapter | PLANNED |
| ch25 | Composite와 Orchestration | PLANNED |
| ch26 | Modular Monolith | PLANNED |
| ch27 | JUnit과 Mockito | PLANNED |
| ch28 | 계층별 테스트 | PLANNED |
| ch29 | 통합 테스트 | PLANNED |
| ch30 | Spring Security | PLANNED |
| ch31 | Logging과 Actuator | PLANNED |
| ch32 | Docker | PLANNED |
| ch33 | GitHub Actions와 CI/CD | PLANNED |
| f01~f04 | 종합 프로젝트 | PLANNED |
