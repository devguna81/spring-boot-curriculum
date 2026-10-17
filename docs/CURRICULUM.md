# Spring Boot 4 실습형 커리큘럼

초급 개발자가 Spring Boot 4로 REST API, DB, 화면, 아키텍처, 테스트, 배포까지 단계적으로 익히도록 구성했다.
모든 챕터는 `examples/` 아래 실행 가능한 예제와 1:1로 연결되고, 블로그 글은 검증된 코드만 근거로 쓴다.

- 기술 스택: [TECH-STACK.md](TECH-STACK.md)

## 전체 구성

총 8개 Part, 36개 챕터 (ch01~ch32 + 종합 프로젝트 f01~f04).

| Part | 주제 | 챕터 |
|---|---|---|
| 01 | Spring Boot 입문 | ch01~ch03 |
| 02 | Spring MVC 및 REST API | ch04~ch07 |
| 03 | Database 및 MyBatis | ch08~ch14 |
| 04 | MapStruct 및 API 명세 | ch15~ch17 |
| 05 | 화면 개발 | ch18~ch21 |
| 06 | 실무 아키텍처 | ch22~ch25 |
| 07 | 테스트 및 운영 | ch26~ch32 |
| 08 | 종합 프로젝트 | f01~f04 |

## Part 01. Spring Boot 입문

| 챕터 | 제목 | 다루는 내용 | 예제 범위 | 선수학습 |
|---|---|---|---|---|
| ch01 | Hello Spring Boot | Spring Framework와 Boot, Boot 4 특징, 개발 환경, Gradle 프로젝트, 디렉터리 구조, 실행 | `GET /hello`, `GET /info`, MockMvc 테스트 | Java 기본 문법 |
| ch02 | 설정과 Profile | application.yml, 외부 설정 우선순위, `@Value`, `@ConfigurationProperties`, Profile | dev/prod Profile별 설정 값 노출, 설정 바인딩 테스트 | ch01 |
| ch03 | IoC, DI, Bean | IoC 컨테이너, 생성자 주입, Bean 등록, Component Scan, Bean 생명주기 | 직접 의존 vs DI 비교, `@Configuration`/`@Bean`, 스캔 범위 실험 | ch01 |

## Part 02. Spring MVC 및 REST API

| 챕터 | 제목 | 다루는 내용 | 예제 범위 | 선수학습 |
|---|---|---|---|---|
| ch04 | HTTP와 REST, Controller | HTTP 메서드, REST 원칙, Controller, `@RequestParam`/`@PathVariable` | 메모리 기반 조회 API (GET) | ch01, ch03 |
| ch05 | CRUD API와 DTO | POST/PUT/PATCH/DELETE, `@RequestBody`, Java Record DTO, `ResponseEntity`, Service 계층 | 메모리 기반 사용자 CRUD, 상태 코드 설계 | ch04 |
| ch06 | Validation | Jakarta Validation, `@Valid`, 제약 애노테이션, 커스텀 검증 | 요청 검증과 오류 응답 | ch05 |
| ch07 | 예외 처리와 공통 응답 | `@RestControllerAdvice`, 공통 API 응답, 오류 코드 체계 | 전역 예외 처리, 공통 응답 래퍼 | ch06 |

## Part 03. Database 및 MyBatis

| 챕터 | 제목 | 다루는 내용 | 예제 범위 | 선수학습 |
|---|---|---|---|---|
| ch08 | DataSource와 H2 | DataSource, 커넥션 풀, H2, 초기 스키마/데이터 | JdbcClient로 H2 조회, 초기 데이터 | ch05 |
| ch09 | MyBatis 기본과 CRUD | Mapper Interface, XML Mapper, `resultMap`, CRUD | 사용자 CRUD (H2) | ch08 |
| ch10 | 동적 SQL, JOIN, 페이징 | `<if>`/`<where>`/`<foreach>`, JOIN 매핑, 페이징 | 검색 조건 조합, 게시글-작성자 JOIN, 페이지 응답 | ch09 |
| ch11 | 트랜잭션 | `@Transactional`, 전파/롤백 규칙, 경계 설계 | 실패 시 롤백 테스트 | ch09 |
| ch12 | Oracle 연결 | Oracle JDBC, Profile 분리, 접속 설정, 로컬 Oracle 컨테이너 | `oracle` Profile, 연결 테스트(분리 실행) | ch02, ch09 |
| ch13 | Oracle JSON | JSON 컬럼, `JSON_VALUE`/`JSON_QUERY`/`JSON_TABLE`, JSON 타입과 CLOB 차이 | JSON 저장과 질의 SQL (Oracle 필요) | ch12 |
| ch14 | JSON과 Java 객체 변환 | Jackson, MyBatis `TypeHandler`로 JSON 컬럼 매핑 | JSON 컬럼 <-> record 변환 | ch13 |

## Part 04. MapStruct 및 API 명세

| 챕터 | 제목 | 다루는 내용 | 예제 범위 | 선수학습 |
|---|---|---|---|---|
| ch15 | DTO, Domain, MapStruct | DTO와 Domain 분리, MapStruct 매핑, 컴파일 타임 생성 코드 | 요청/응답 DTO 변환 | ch09 |
| ch16 | OpenAPI와 Swagger UI | springdoc, 애노테이션, Swagger UI | 기존 API 문서화 | ch07 |
| ch17 | API Contract | Request/Response Contract, 내부 DTO와 외부 Contract 분리, 변경 관리 | 외부 Contract 패키지 분리 | ch15, ch16 |

## Part 05. 화면 개발

| 챕터 | 제목 | 다루는 내용 | 예제 범위 | 선수학습 |
|---|---|---|---|---|
| ch18 | Thymeleaf 기초 | Model, Template, 반복/조건, Layout | 목록/상세 화면 | ch05 |
| ch19 | Thymeleaf CRUD | Form 바인딩, 검증 오류 표시, PRG 패턴 | 사용자 등록/수정/삭제 화면 | ch06, ch18 |
| ch20 | React 기초 | Vite, 컴포넌트, Hooks, Router | 정적 목록 화면 | JavaScript 기본 |
| ch21 | React와 REST API 연동 | fetch, CORS, React CRUD, Thymeleaf와 비교 | API 연동 CRUD 화면 | ch05, ch20 |

## Part 06. 실무 아키텍처

| 챕터 | 제목 | 다루는 내용 | 예제 범위 | 선수학습 |
|---|---|---|---|---|
| ch22 | Layered Architecture와 SOLID | 계층 책임, SOLID, DIP, Interface/Implementation | 계층 위반 코드 리팩터링 | ch09 |
| ch23 | Contract와 Port/Adapter | Domain Model, Contract Interface, Port/Adapter, Internal/External API | 외부 연동을 Port 뒤로 숨김 | ch17, ch22 |
| ch24 | Composite와 Orchestration | Composite, Orchestration, 책임 분리 | 여러 서비스를 조합하는 Orchestrator | ch23 |
| ch25 | Modular Monolith | 모듈 경계, 의존성 방향, 순환참조 차단, 트랜잭션 경계 | 모듈 분리와 의존성 검증 테스트 | ch11, ch24 |

Contract는 Spring Boot 내장 기능이 아니라 API 계약과 인터페이스 기반 설계 개념으로 설명한다.

## Part 07. 테스트 및 운영

| 챕터 | 제목 | 다루는 내용 | 예제 범위 | 선수학습 |
|---|---|---|---|---|
| ch26 | JUnit과 Mockito | 단위 테스트, Mock, 검증 | Service 단위 테스트 | ch05 |
| ch27 | 계층별 테스트 | Controller, Service, MyBatis 테스트 | `@WebMvcTest`, `@MybatisTest` | ch09, ch26 |
| ch28 | 통합 테스트 | `@SpringBootTest`, 테스트 데이터, 컨테이너 | 전 계층 통합 시나리오 | ch27 |
| ch29 | Spring Security | 인증/인가, SecurityFilterChain, 테스트 | 로그인과 권한별 접근 제어 | ch07 |
| ch30 | Logging과 Actuator | 로깅 설정, 구조화 로그, Actuator, 헬스 체크 | 로그 레벨, health/metrics | ch02 |
| ch31 | Docker | 멀티 스테이지 빌드, 이미지 실행, compose | 애플리케이션 이미지 | ch01 |
| ch32 | GitHub Actions와 CI/CD | 워크플로, 캐시, 아티팩트, 운영 환경 설정 | 빌드/테스트/이미지 빌드 파이프라인 | ch31 |

## Part 08. 종합 프로젝트: 사용자·게시판 관리 시스템

위치: `projects/final-project/`

| 챕터 | 제목 | 내용 |
|---|---|---|
| f01 | 설계 | 요구사항, 모듈 구조, DB 설계, Contract 정의 |
| f02 | 백엔드 | 사용자/게시글 CRUD, 검색과 페이징, MyBatis, Oracle JSON, MapStruct, Validation, 예외 처리 |
| f03 | 화면과 문서 | Thymeleaf 화면, React 화면, Swagger |
| f04 | 품질과 배포 | 테스트, Spring Security, Docker, GitHub Actions |

## 진행 원칙

1. 코드 구현 -> 빌드/테스트 -> GitHub 반영 -> MD 초안 -> 사용자 검토 -> 게시 순서를 지킨다.
2. 챕터는 순차 진행하고, 이전 챕터가 `VERIFIED` 이상이어야 다음을 시작한다.
3. Oracle이 필요한 검증은 로컬에서 실행하지 못하면 `미검증`으로 기록하고 성공으로 보고하지 않는다.
4. 챕터를 시작할 때 사용할 라이브러리가 Boot 4에서 지원되는지 먼저 확인하고 `TECH-STACK.md`에 반영한다.
