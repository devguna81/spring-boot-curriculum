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
| Test | JUnit 5 / Mockito / Spring Boot Test | Boot BOM 관리 | ch01 | `spring-boot-starter-webmvc-test` |
| Persistence | mybatis-spring-boot-starter | 4.1.0 | ch09 | 부모 POM의 `spring-boot.version`이 4.1.0 (MyBatis 3.5.19) |
| Persistence (확장) | mybatis-plus-spring-boot4-starter + mybatis-plus-jsqlparser | 3.5.17 | ch12 | Boot 4 전용 스타터. 아래 "MyBatis-Plus 검토" 참조 |
| API 문서 | springdoc-openapi-starter-webmvc-ui | 3.1.1 | ch17 | 부모 POM이 `spring-boot-starter-parent:4.1.0` |
| Mapping | MapStruct | 1.6.3 | ch16 | 최신 안정판. 1.7.0은 Beta라 제외 |
| Template | spring-boot-starter-thymeleaf (Thymeleaf 3.1.5) | Boot BOM 관리 | ch19 | 서버 렌더링 화면 |
| 구조 검사 | ArchUnit | 1.4.1 | ch18 | JUnit 연동 모듈 없이 코어만 사용 |
| DB (개발) | H2 | Boot BOM 관리 | ch08 | Boot가 버전을 관리 |
| DB (운영 가정) | Oracle JDBC ojdbc11 | 23.26.3.0.0 | ch13 | Java 11+ 용 드라이버, Maven Central 최신 |
| Frontend | React + Vite | 해당 챕터에서 확정 | ch21 | Node LTS 기준으로 그 시점에 재확인 |

## Boot 4에서 달라진 점

Boot 3 자료의 코드를 그대로 가져오면 컴파일되지 않는 지점이다. 해당 챕터에서 다시 다룬다.

| 항목 | Boot 3.x | Boot 4.x |
|---|---|---|
| Web MVC 스타터 | `spring-boot-starter-web` | `spring-boot-starter-webmvc` |
| Web MVC 테스트 스타터 | `spring-boot-starter-test` | `spring-boot-starter-webmvc-test` |
| `@AutoConfigureMockMvc` | `org.springframework.boot.test.autoconfigure.web.servlet` | `org.springframework.boot.webmvc.test.autoconfigure` |
| `@WebMvcTest` | `org.springframework.boot.test.autoconfigure.web.servlet` | `org.springframework.boot.webmvc.test.autoconfigure` |
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
