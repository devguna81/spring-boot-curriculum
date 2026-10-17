# TECH-STACK

확정 버전과 근거. 버전은 루트 `gradle.properties`와 각 예제의 `build.gradle`이 단일 소스이며, 변경하면 이 문서도 함께 갱신한다.
조사 기준일: 2026-10-17 (Maven Central, Spring Initializr 실측)

## 확정 버전

| 구분 | 기술 | 버전 | 도입 챕터 | 근거 |
|---|---|---|---|---|
| Language | Java (Temurin) | 21 LTS | ch01 | Spring Boot 4 지원, LTS |
| Framework | Spring Boot | 4.1.1 | ch01 | Spring Initializr 기본 안정 버전 (4.2.0은 M2/SNAPSHOT) |
| Build | Gradle | 9.8.1 (Wrapper) | ch01 | Initializr가 생성한 Wrapper |
| Gradle 플러그인 | io.spring.dependency-management | 1.1.7 | ch01 | Initializr 기본값 |
| Test | JUnit 5 / Mockito / Spring Boot Test | Boot BOM 관리 | ch01 | `spring-boot-starter-webmvc-test` |
| Persistence | mybatis-spring-boot-starter | 4.1.0 | ch09 | 부모 POM의 `spring-boot.version`이 4.1.0 (MyBatis 3.5.19) |
| API 문서 | springdoc-openapi-starter-webmvc-ui | 3.1.1 | ch16 | 부모 POM이 `spring-boot-starter-parent:4.1.0` |
| Mapping | MapStruct | 1.6.3 | ch15 | 최신 안정판. 1.7.0은 Beta라 제외 |
| DB (개발) | H2 | Boot BOM 관리 | ch08 | Boot가 버전을 관리 |
| DB (운영 가정) | Oracle JDBC ojdbc11 | 23.26.3.0.0 | ch12 | Java 11+ 용 드라이버, Maven Central 최신 |
| Frontend | React + Vite | 해당 챕터에서 확정 | ch20 | Node LTS 기준으로 그 시점에 재확인 |

## Boot 4에서 달라진 점 (실측)

예전 글과 예제를 그대로 복사하면 컴파일이 안 되는 지점이다. 해당 챕터에서 다시 다룬다.

| 항목 | Boot 3.x | Boot 4.x |
|---|---|---|
| Web MVC 스타터 | `spring-boot-starter-web` | `spring-boot-starter-webmvc` |
| Web MVC 테스트 스타터 | `spring-boot-starter-test` | `spring-boot-starter-webmvc-test` |
| `@AutoConfigureMockMvc` | `org.springframework.boot.test.autoconfigure.web.servlet` | `org.springframework.boot.webmvc.test.autoconfigure` |
| `@WebMvcTest` | `org.springframework.boot.test.autoconfigure.web.servlet` | `org.springframework.boot.webmvc.test.autoconfigure` |
| 내장 Tomcat | 10.x | 11.x (ch01 실행 로그: Apache Tomcat/11.0.24) |

위 패키지 경로는 `spring-boot-webmvc-test-4.1.1.jar`의 클래스 목록으로 확인했다.

## 미확정 · 추후 검증 항목

- MyBatis 4.1.0 + Boot 4.1.1 조합: 부모 POM 기준으로만 확인했다. ch09에서 실제 빌드로 검증한 뒤 이 표를 갱신한다.
- springdoc 3.1.1 + Boot 4.1.1: 위와 동일하게 ch16에서 검증한다.
- Oracle: 로컬에 Oracle 인스턴스가 없다. Oracle 의존 테스트는 `oracle` 프로파일로 분리하고, 실행하지 못하면 `미검증`으로 기록한다.

## 개발 환경

| 항목 | 값 |
|---|---|
| 필수 | JDK 21 (Gradle Toolchain이 21을 요구한다) |
| 선택 | Docker (Oracle XE, 컨테이너 빌드 검증용) |
| IDE | IntelliJ IDEA 권장 |
