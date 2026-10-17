# TECH-STACK

커리큘럼에서 사용하는 기술과 버전. 버전은 루트 `gradle.properties`와 각 예제의 `build.gradle`이 기준이며, 변경하면 이 문서도 함께 고친다.
기준일: 2026-10-17

## 확정 버전

| 구분 | 기술 | 버전 | 도입 챕터 | 근거 |
|---|---|---|---|---|
| Language | Java (Temurin) | 21 LTS | ch01 | Spring Boot 4 지원, LTS |
| Framework | Spring Boot | 4.1.1 | ch01 | Spring Initializr 기본 안정 버전 (4.2.0은 M2/SNAPSHOT) |
| Build | Gradle | 9.8.1 (Wrapper) | ch01 | Initializr가 생성한 Wrapper |
| Gradle 플러그인 | io.spring.dependency-management | 1.1.7 | ch01 | Initializr 기본값 |
| Test | JUnit Jupiter 6.0.3 / Mockito 5.23.0 / AssertJ 3.27.7 / Spring Boot Test | Boot BOM 관리 | ch01, ch27 | `spring-boot-starter-webmvc-test`, 서비스 단위 테스트는 `spring-boot-starter-test` (ch27) |
| Persistence | mybatis-spring-boot-starter | 4.1.0 | ch09 | 부모 POM의 `spring-boot.version`이 4.1.0 (MyBatis 3.5.19) |
| Persistence (확장) | mybatis-plus-spring-boot4-starter + mybatis-plus-jsqlparser | 3.5.17 | ch12, f02 | Boot 4 전용 스타터. 아래 "MyBatis-Plus 검토" 참조 |
| API 문서 | springdoc-openapi-starter-webmvc-ui | 3.1.1 | ch17, f03 | 부모 POM이 `spring-boot-starter-parent:4.1.0` |
| Mapping | MapStruct | 1.6.3 | ch16, f02 | 최신 안정판. 1.7.0은 Beta라 제외 |
| Template | spring-boot-starter-thymeleaf (Thymeleaf 3.1.5) | Boot BOM 관리 | ch19, f03 | 서버 렌더링 화면 |
| 구조 검사 | ArchUnit | 1.4.1 | ch18, ch23, ch24, ch26, f01~f04 | JUnit 연동 모듈 없이 코어만 사용 |
| 슬라이스 테스트 | mybatis-spring-boot-starter-test (`@MybatisTest`) | 4.1.0 | ch28 | mybatis-spring-boot-starter와 같은 버전 |
| 컨테이너 테스트 | Testcontainers (`testcontainers-junit-jupiter`, `testcontainers-postgresql`) / spring-boot-testcontainers | 2.0.5 (Boot BOM 관리) | ch29 | 모듈 이름이 1.x와 다르다(`postgresql` -> `testcontainers-postgresql`) |
| DB (컨테이너) | PostgreSQL 서버 / JDBC 드라이버 | 17-alpine / 42.7.13 (Boot BOM 관리) | ch29, ch32 | 실제 DB와의 차이 확인, compose 예제 |
| 보안 | spring-boot-starter-security (Spring Security 7.1.1) / spring-boot-starter-security-test | Boot BOM 관리 | ch30, f04 | |
| 운영 | spring-boot-starter-actuator (Micrometer 1.17.1) | Boot BOM 관리 | ch31, ch32, f04 | |
| 컨테이너 | Docker / Docker Compose | Engine 29.5.2 / Compose 5.1.4 (검증 환경) | ch32, f04 | 기본 이미지 `eclipse-temurin:21-jdk-jammy`, `-jre-jammy` |
| CI | GitHub Actions (checkout v7, setup-java v6, setup-gradle v6, setup-node v7, upload-artifact v7, docker/setup-buildx v4, docker/build-push v7) | 2026-10-17 최신 메이저 | ch33, f04 | 이전 v4는 CI 로그에서 사용 중단 경고 |
| DB (개발) | H2 | Boot BOM 관리 | ch08, f01 | Boot가 버전을 관리 |
| DB (운영 가정) | Oracle JDBC ojdbc11 | 23.26.3.0.0 | ch13, f01 | Java 11+ 용 드라이버, Maven Central 최신 |
| Frontend | React / React DOM | 19.3.1 | ch21, f03 | npm 최신 안정판, 정확한 버전 고정 |
| Frontend 빌드 | Vite / @vitejs/plugin-react | 8.3.4 / 6.1.2 | ch21, f03 | Node 22.12 이상 필요 |
| Frontend 라우팅 | react-router | 8.4.0 | ch21, f03 | v7부터 `react-router` 패키지로 통합 |
| Frontend 테스트 | Vitest / jsdom / Testing Library | 5.0.5 / 30.1.3 / react 16.3.3 | ch21 | 사용자 관점 테스트 |

## Boot 4에서 달라진 점

Boot 3 자료의 코드를 그대로 가져오면 컴파일되지 않는 지점이다. 해당 챕터에서 다시 다룬다.

| 항목 | Boot 3.x | Boot 4.x |
|---|---|---|
| Web MVC 스타터 | `spring-boot-starter-web` | `spring-boot-starter-webmvc` |
| Web MVC 테스트 스타터 | `spring-boot-starter-test` | `spring-boot-starter-webmvc-test` |
| `@AutoConfigureMockMvc` | `org.springframework.boot.test.autoconfigure.web.servlet` | `org.springframework.boot.webmvc.test.autoconfigure` |
| `@WebMvcTest` | `org.springframework.boot.test.autoconfigure.web.servlet` | `org.springframework.boot.webmvc.test.autoconfigure` |
| `@MockBean` 대체 | `@MockBean` (`org.springframework.boot.test.mock.mockito`) | `@MockitoBean` (`org.springframework.test.context.bean.override.mockito`) |
| 보안 테스트 의존성 | `spring-security-test` | `spring-boot-starter-security-test` (`spring-security-test`만으로는 MockMvc에서 `@WithMockUser`가 적용되지 않았다) |
| `HealthIndicator` 패키지 | `org.springframework.boot.actuate.health` | `org.springframework.boot.health.contributor` |
| 내장 Tomcat | 10.x | 11.x (ch01 실행 로그: Apache Tomcat/11.0.24) |


## MyBatis-Plus

| 항목 | 내용 |
|---|---|
| 스타터 | `com.baomidou:mybatis-plus-spring-boot4-starter:3.5.17` (Boot 3용 `-spring-boot3-starter`와 별도 아티팩트) |
| 기준 버전 | 스타터가 Boot 4.0.1, mybatis-spring 4.0.0 기준으로 만들어졌다. 이 저장소의 Boot 4.1.1, 공식 MyBatis 스타터(mybatis-spring 4.1.0)보다 한 단계 이전 버전이다 |
| 페이징 | 3.5.9 이후 `PaginationInnerInterceptor`를 쓰려면 `mybatis-plus-jsqlparser` 의존성을 따로 추가해야 한다 |
| 사용 범위 | MyBatis를 대체하지 않고 ch12에서 단순 CRUD를 줄이는 용도로 함께 쓴다. XML Mapper와 같이 쓸 수 있다 |

Oracle 시퀀스(`@KeySequence`)와의 조합은 이 저장소에서 검증하지 않았다. JSON 컬럼은 MyBatis `TypeHandler`로 매핑한다(ch15). Boot 마이너 버전을 올릴 때는 스타터 호환성을 다시 확인한다.

## 챕터별로 확인할 사항

- ch09: MyBatis starter 4.1.0과 Boot 4.1.1 조합
- ch17: springdoc 3.1.1과 Boot 4.1.1 조합
- ch13~ch15: Oracle 접속이 필요한 테스트는 `ORACLE_URL` 환경 변수가 있을 때만 실행하고, 없으면 건너뛴다. 기본 Profile은 H2다. CI에서는 일회용 Oracle Free 컨테이너(`gvenzl/oracle-free:23-slim-faststart`)를 띄워 별도 잡으로 실행한다.

## 개발 환경

| 항목 | 값 |
|---|---|
| 필수 | JDK 21 (Gradle Toolchain이 21을 요구한다) |
| 선택 | Docker (Oracle Free 컨테이너, 컨테이너 실습용) |
| IDE | IntelliJ IDEA 권장 |
