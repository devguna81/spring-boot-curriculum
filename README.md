# Spring Boot Curriculum - 단계별 Spring Boot 4 학습 예제 코드 저장소

jqdom.com의 **Spring Boot 4 실습형 커리큘럼** 시리즈(8개 Part, 총 33개 챕터 + 종합 프로젝트) 공식 예제 코드 저장소입니다.

Spring Boot 입문부터 REST API, MyBatis와 Oracle JSON, MapStruct와 OpenAPI, Thymeleaf와 React 화면, 실무 아키텍처(Contract, Port/Adapter, Modular Monolith), 테스트와 Docker/GitHub Actions 운영, 그리고 사용자·게시판 관리 종합 프로젝트까지 단계별로 빌드하고 실행할 수 있도록 구성합니다.

---

## 요구 사항 (Prerequisites)

- **Java**: OpenJDK 21 (Java 21 LTS) 필수
  - 버전 확인: `java -version` 및 `javac -version`
  - 권장 배포판: Eclipse Temurin 21, Amazon Corretto 21, Oracle OpenJDK 21
  - 프로젝트가 Gradle Toolchain으로 Java 21을 요구하므로 JDK 17 이하에서는 빌드되지 않습니다.
- **프레임워크**: Spring Boot 4.1.1 (Spring Framework 7)
- **빌드 도구**: Gradle 9.8.1 (프로젝트 내 Gradle Wrapper 포함, 별도 설치 불필요)
- **운영체제**: Linux, macOS, Windows
- **선택 사항**: Docker (컨테이너 챕터와 Oracle 실습), Node.js LTS (React 챕터)

확정된 라이브러리 버전과 근거는 [docs/TECH-STACK.md](docs/TECH-STACK.md)에 정리되어 있습니다.

---

## 프로젝트 체크아웃 및 동기화 (Checkout & Sync)

### 1. 최초 저장소 복제 (Git Clone)

터미널 또는 콘솔 창에서 아래 명령어를 실행하여 로컬 환경에 저장소를 복제합니다:

```bash
# 저장소 복제
git clone https://github.com/devguna81/spring-boot-curriculum.git

# 프로젝트 디렉토리 이동
cd spring-boot-curriculum
```

### 2. 최신 코드 반영 (Git Pull)

원격 저장소의 최신 예제 코드 및 수정사항을 동기화하려면 아래 명령어를 사용합니다:

```bash
# main 브랜치 최신 커밋 가져오기
git pull origin main

# 로컬 작업 내용이 있어 충돌이 발생하는 경우
git stash
git pull origin main
git stash pop
```

---

## 프로젝트 빌드 및 실행 가이드 (Build & Run)

본 프로젝트는 챕터별 예제가 하위 모듈로 구성된 **Gradle 멀티 프로젝트(Multi-project)** 구조입니다. 모듈은 `examples/` 아래에 있으며, 챕터가 추가될 때마다 `settings.gradle`의 `include`가 늘어납니다.

```text
spring-boot-curriculum/
├── settings.gradle        # 모듈 목록과 플러그인 버전
├── build.gradle           # 모든 모듈의 공통 설정 (Java 21, JUnit)
├── gradle.properties      # Spring Boot 버전 (단일 소스)
├── gradlew / gradlew.bat  # Gradle Wrapper
├── examples/              # 챕터별 예제 모듈
├── projects/              # 종합 프로젝트
└── docs/                  # 커리큘럼, 기술 스택, 챕터별 문서
```

### 1. Gradle Wrapper 실행 권한 부여 (Linux / macOS)

저장소를 처음 복제한 후 `gradlew` 스크립트에 실행 권한이 없다면 설정합니다:

```bash
chmod +x gradlew
```

> **Windows 사용자 참고:** Windows 환경에서는 `./gradlew` 대신 `gradlew.bat`을 실행하거나 PowerShell에서 `.\gradlew`를 입력합니다. 이 문서의 모든 `./gradlew` 명령은 `gradlew.bat`으로 바꿔 사용할 수 있습니다.

### 2. 전체 프로젝트 컴파일 및 빌드 (Full Build)

루트 디렉토리에서 모든 모듈을 일괄 컴파일하고 테스트합니다:

```bash
# 전체 모듈 Java 소스 컴파일
./gradlew compileJava

# 전체 빌드 및 테스트 실행
./gradlew build

# 이전 빌드 결과를 지우고 새로 빌드
./gradlew clean build
```

빌드가 성공하면 마지막에 `BUILD SUCCESSFUL`이 출력됩니다. Oracle 접속이 필요한 테스트는 별도 프로파일로 분리되어 있어 기본 빌드에서는 실행되지 않습니다.

### 3. 특정 챕터 모듈별 빌드 (Targeted Build)

특정 챕터 모듈만 지정하여 빠르게 빌드할 수 있습니다. 모듈 경로는 `:examples:<챕터-디렉토리>` 형식입니다:

```bash
# Chapter 01 (Hello Spring Boot) 모듈 컴파일
./gradlew :examples:ch01-hello-spring:compileJava

# Chapter 01 모듈 테스트만 실행
./gradlew :examples:ch01-hello-spring:test

# Chapter 01 모듈 빌드 (컴파일 + 테스트 + JAR 생성)
./gradlew :examples:ch01-hello-spring:build
```

### 4. 챕터 예제 실행 (Run)

#### Gradle로 바로 실행

```bash
# Chapter 01 애플리케이션 실행 (기본 포트 8080)
./gradlew :examples:ch01-hello-spring:bootRun
```

콘솔에 `Tomcat started on port 8080` 과 `Started Ch01Application` 로그가 보이면 기동이 끝난 것입니다. 종료는 `Ctrl + C` 입니다.

#### 실행 가능한 JAR로 실행

```bash
# 1. 실행 가능한 JAR 생성
./gradlew :examples:ch01-hello-spring:bootJar

# 2. JAR 실행
java -jar examples/ch01-hello-spring/build/libs/ch01-hello-spring-0.0.1-SNAPSHOT.jar

# 3. 포트를 바꿔서 실행 (8080이 사용 중일 때)
java -jar examples/ch01-hello-spring/build/libs/ch01-hello-spring-0.0.1-SNAPSHOT.jar --server.port=8081
```

#### API 호출로 동작 확인

애플리케이션이 실행 중인 상태에서 다른 터미널에서 호출합니다:

```bash
curl http://localhost:8080/hello
# Hello, Spring Boot!

curl "http://localhost:8080/hello?name=devguna"
# Hello, devguna!

curl http://localhost:8080/info
# {"name":"ch01-hello-spring","javaVersion":"21.0.11","springBootVersion":"4.1.1"}
```

브라우저에서 `http://localhost:8080/hello` 를 열어도 됩니다. `javaVersion` 값은 설치된 JDK에 따라 달라집니다.

#### Profile을 지정해서 실행 (Chapter 02)

```bash
# Profile 없이 실행: application.yml만 적용 (포트 8080)
./gradlew :examples:ch02-config-profile:bootRun

# dev Profile (포트 8081)
./gradlew :examples:ch02-config-profile:bootRun --args='--spring.profiles.active=dev'

# 명령행 인자로 설정 덮어쓰기
./gradlew :examples:ch02-config-profile:bootRun --args='--spring.profiles.active=dev --app.greeting.prefix=CLI'
```

```bash
curl "http://localhost:8081/greeting?name=devguna"
# Hi (dev), devguna!

curl http://localhost:8081/config
```

#### Bean 등록과 주입 확인 (Chapter 03)

```bash
./gradlew :examples:ch03-ioc-di-bean:bootRun
```

```bash
curl "http://localhost:8080/orders?item=book"
# [email] order placed: book

curl "http://localhost:8080/orders?item=book&urgent=true"
# [sms] order placed: book

curl http://localhost:8080/beans
```

#### 조회 API 호출 (Chapter 04)

```bash
./gradlew :examples:ch04-rest-controller:bootRun
```

```bash
curl http://localhost:8080/users
curl "http://localhost:8080/users?keyword=kim&limit=1"
curl -i http://localhost:8080/users/999   # 404
```

#### CRUD API 호출 (Chapter 05)

```bash
./gradlew :examples:ch05-crud-api:bootRun
```

```bash
curl -i -X POST http://localhost:8080/users -H "Content-Type: application/json" \
  -d '{"name":"kim","email":"kim@example.com","age":28}'
curl -X PATCH http://localhost:8080/users/1 -H "Content-Type: application/json" -d '{"age":29}'
curl -i -X DELETE http://localhost:8080/users/1
```

#### 검증 실패 확인 (Chapter 06)

```bash
./gradlew :examples:ch06-validation:bootRun
```

```bash
curl -i -X POST http://localhost:8080/users -H "Content-Type: application/json" \
  -d '{"name":"k","email":"kim@gmail.com","age":-1}'   # 400
curl -i "http://localhost:8080/users?limit=0"                 # 400
```

#### 공통 응답 확인 (Chapter 07)

```bash
./gradlew :examples:ch07-exception-handling:bootRun
```

```bash
curl -i http://localhost:8080/users/999     # 404, error.code=NOT_FOUND
curl -i http://localhost:8080/demo/error    # 500, 내부 메시지는 응답에 없음
```

#### 데이터베이스와 커넥션 풀 확인 (Chapter 08)

```bash
./gradlew :examples:ch08-datasource-h2:bootRun
curl http://localhost:8080/db/info
curl http://localhost:8080/users
```

#### Oracle에 연결해서 실행 (Chapter 13~15)

Oracle 테스트와 oracle Profile 실행에는 Oracle 21c 이상이 필요합니다. 로컬에서는 컨테이너로 띄웁니다.

```bash
cd examples/ch13-oracle-connection
ORACLE_SYS_PASSWORD='<임의 값>' ORACLE_APP_PASSWORD='<임의 값>' docker compose up -d
```

```bash
export ORACLE_URL='jdbc:oracle:thin:@//localhost:1521/FREEPDB1'
export ORACLE_USERNAME='curriculum'
export ORACLE_PASSWORD='<위에서 정한 ORACLE_APP_PASSWORD>'
./gradlew :examples:ch13-oracle-connection:test
./gradlew :examples:ch13-oracle-connection:bootRun --args='--spring.profiles.active=oracle'
```

`ORACLE_URL` 환경 변수가 없으면 Oracle 테스트는 건너뜁니다.

#### API 문서 보기 (Chapter 17)

```bash
./gradlew :examples:ch17-openapi:bootRun
# 브라우저에서 http://localhost:8080/swagger-ui.html
curl http://localhost:8080/v3/api-docs
```

#### 계약 저장본 갱신 (Chapter 18)

계약을 의도해서 바꿨다면 호환성을 확인한 뒤 저장본을 갱신합니다.

```bash
./gradlew :examples:ch18-api-contract:test -Dcontract.update=true
```

#### 화면 보기 (Chapter 19, 20)

```bash
./gradlew :examples:ch19-thymeleaf:bootRun
# 브라우저에서 http://localhost:8080/users

./gradlew :examples:ch20-thymeleaf-crud:bootRun
# 브라우저에서 http://localhost:8080/users (목록에서 등록, 수정, 삭제)
```

#### React 화면 (Chapter 21)

Node 22.12 이상이 필요합니다. Gradle이 아니라 npm으로 실행합니다.

```bash
cd examples/ch21-react-basics
npm ci
npm run dev      # http://localhost:5173
npm test
```

### 5. 테스트 결과 확인

```bash
./gradlew :examples:ch01-hello-spring:test
```

테스트 결과 HTML 리포트는 각 모듈의 `build/reports/tests/test/index.html` 에 생성됩니다. 테스트 실패 시 원인 분석에 사용합니다.

### 6. Final Project: 사용자·게시판 관리 시스템

커리큘럼의 종합 실습은 `projects/final-project` 에서 진행합니다. 사용자·게시글 CRUD, 검색과 페이징, MyBatis, Oracle JSON, MapStruct, Swagger, Thymeleaf와 React 화면, Spring Security, Docker, GitHub Actions를 모두 적용하는 프로젝트입니다. 구현되면 이곳에 실행 방법을 추가합니다.

---

## IDE 환경 설정 (IDE Setup)

### IntelliJ IDEA
1. IntelliJ IDEA 실행 후 **[Open]** 메뉴 선택
2. `spring-boot-curriculum` 루트 디렉토리(`settings.gradle`이 있는 위치) 선택
3. `Import Gradle Project` 대화상자에서 프로젝트 신뢰(Trust Project) 선택 및 동기화 진행
4. `File > Project Structure > Project SDK` 와 `Settings > Build Tools > Gradle > Gradle JVM` 이 모두 **Java 21** 로 지정되어 있는지 확인
5. 프로젝트 탐색기에서 `examples/ch01-hello-spring/src/main/java/.../Ch01Application.java` 를 열고 `main` 메서드 라인의 실행 버튼 클릭

### Visual Studio Code
1. `Extension Pack for Java` 및 `Spring Boot Extension Pack` 확장 설치
2. `File > Open Folder...` 메뉴로 `spring-boot-curriculum` 디렉토리 열기
3. Java Language Server 빌드 완료 후 각 `*Application.java` 상단의 `Run | Debug` 링크를 통해 실행

---

## 전체 커리큘럼 및 모듈 구성 (Curriculum & Modules)

Part별 구성은 다음과 같습니다. 챕터별 선수학습과 예제 범위는 [docs/CURRICULUM.md](docs/CURRICULUM.md)에 있습니다.

| Part | 주제 | 챕터 |
|:---|:---|:---:|
| **Part 01** | Spring Boot 입문 | ch01~ch03 |
| **Part 02** | Spring MVC 및 REST API | ch04~ch07 |
| **Part 03** | Database 및 MyBatis, MyBatis-Plus, Oracle JSON | ch08~ch15 |
| **Part 04** | MapStruct 및 API 명세 (OpenAPI, Contract) | ch16~ch18 |
| **Part 05** | 화면 개발 (Thymeleaf, React) | ch19~ch22 |
| **Part 06** | 실무 아키텍처 | ch23~ch26 |
| **Part 07** | 테스트 및 운영 | ch27~ch33 |
| **Part 08** | 종합 프로젝트 | f01~f04 |

### 챕터 목록

| 챕터 | 주요 학습 내용 | 예제 모듈 |
|:---|:---|:---|
| **ch01. Hello Spring Boot** | Spring Framework와 Boot, Boot 4 특징, 개발 환경, Gradle 프로젝트, 디렉터리 구조, 실행 | [`examples/ch01-hello-spring`](examples/ch01-hello-spring) |
| **ch02. 설정과 Profile** | application.yml, 외부 설정 우선순위, `@Value`, `@ConfigurationProperties`, Profile | [`examples/ch02-config-profile`](examples/ch02-config-profile) |
| **ch03. IoC, DI, Bean** | IoC 컨테이너, 생성자 주입, Bean 등록, Component Scan, Bean 생명주기 | [`examples/ch03-ioc-di-bean`](examples/ch03-ioc-di-bean) |
| **ch04. HTTP와 REST, Controller** | HTTP 메서드, REST 원칙, Controller, `@RequestParam`/`@PathVariable` | [`examples/ch04-rest-controller`](examples/ch04-rest-controller) |
| **ch05. CRUD API와 DTO** | POST/PUT/PATCH/DELETE, `@RequestBody`, Java Record DTO, `ResponseEntity`, Service 계층 | [`examples/ch05-crud-api`](examples/ch05-crud-api) |
| **ch06. Validation** | Jakarta Validation, `@Valid`, 제약 애노테이션, 커스텀 검증 | [`examples/ch06-validation`](examples/ch06-validation) |
| **ch07. 예외 처리와 공통 응답** | `@RestControllerAdvice`, 공통 API 응답, 오류 코드 체계 | [`examples/ch07-exception-handling`](examples/ch07-exception-handling) |
| **ch08. DataSource와 H2** | DataSource, 커넥션 풀, H2, 초기 스키마/데이터 | [`examples/ch08-datasource-h2`](examples/ch08-datasource-h2) |
| **ch09. MyBatis 기본과 CRUD** | Mapper Interface, XML Mapper, `resultMap`, CRUD | [`examples/ch09-mybatis-basics`](examples/ch09-mybatis-basics) |
| **ch10. 동적 SQL, JOIN, 페이징** | `<if>`/`<where>`/`<foreach>`, JOIN 매핑, 페이징 | [`examples/ch10-dynamic-sql`](examples/ch10-dynamic-sql) |
| **ch11. 트랜잭션** | `@Transactional`, 전파/롤백 규칙, 경계 설계 | [`examples/ch11-transaction`](examples/ch11-transaction) |
| **ch12. MyBatis-Plus로 반복 CRUD 줄이기** | `BaseMapper`, `LambdaQueryWrapper`, 페이징 플러그인, XML Mapper와 공존, MyBatis와의 선택 기준 | [`examples/ch12-mybatis-plus`](examples/ch12-mybatis-plus) |
| **ch13. Oracle 연결** | Oracle JDBC, Profile 분리, 접속 설정, 로컬 Oracle 컨테이너 | [`examples/ch13-oracle-connection`](examples/ch13-oracle-connection) |
| **ch14. Oracle JSON** | JSON 컬럼, `JSON_VALUE`/`JSON_QUERY`/`JSON_TABLE`, JSON 타입과 CLOB 차이 | [`examples/ch14-oracle-json`](examples/ch14-oracle-json) |
| **ch15. JSON과 Java 객체 변환** | Jackson, MyBatis `TypeHandler`로 JSON 컬럼 매핑 | [`examples/ch15-json-typehandler`](examples/ch15-json-typehandler) |
| **ch16. DTO, Domain, MapStruct** | DTO와 Domain 분리, MapStruct 매핑, 컴파일 타임 생성 코드 | [`examples/ch16-mapstruct`](examples/ch16-mapstruct) |
| **ch17. OpenAPI와 Swagger UI** | springdoc, 애노테이션, Swagger UI | [`examples/ch17-openapi`](examples/ch17-openapi) |
| **ch18. API Contract** | Request/Response Contract, 내부 DTO와 외부 Contract 분리, 변경 관리 | [`examples/ch18-api-contract`](examples/ch18-api-contract) |
| **ch19. Thymeleaf 기초** | Model, Template, 반복/조건, Layout | [`examples/ch19-thymeleaf`](examples/ch19-thymeleaf) |
| **ch20. Thymeleaf CRUD** | Form 바인딩, 검증 오류 표시, PRG 패턴 | [`examples/ch20-thymeleaf-crud`](examples/ch20-thymeleaf-crud) |
| **ch21. React 기초** | Vite, 컴포넌트, Hooks, Router | [`examples/ch21-react-basics`](examples/ch21-react-basics) |
| **ch22. React와 REST API 연동** | fetch, CORS, React CRUD, Thymeleaf와 비교 | - |
| **ch23. Layered Architecture와 SOLID** | 계층 책임, SOLID, DIP, Interface/Implementation | - |
| **ch24. Contract와 Port/Adapter** | Domain Model, Contract Interface, Port/Adapter, Internal/External API | - |
| **ch25. Composite와 Orchestration** | Composite, Orchestration, 책임 분리 | - |
| **ch26. Modular Monolith** | 모듈 경계, 의존성 방향, 순환참조 차단, 트랜잭션 경계 | - |
| **ch27. JUnit과 Mockito** | 단위 테스트, Mock, 검증 | - |
| **ch28. 계층별 테스트** | Controller, Service, MyBatis 테스트 | - |
| **ch29. 통합 테스트** | `@SpringBootTest`, 테스트 데이터, 컨테이너 | - |
| **ch30. Spring Security** | 인증/인가, SecurityFilterChain, 테스트 | - |
| **ch31. Logging과 Actuator** | 로깅 설정, 구조화 로그, Actuator, 헬스 체크 | - |
| **ch32. Docker** | 멀티 스테이지 빌드, 이미지 실행, compose | - |
| **ch33. GitHub Actions와 CI/CD** | 워크플로, 캐시, 아티팩트, 운영 환경 설정 | - |
| **f01~f04. 종합 프로젝트** | 설계, 백엔드, 화면과 문서, 품질과 배포 | `projects/final-project` |

예제 모듈이 `-`로 표시된 챕터는 아직 추가되지 않았습니다.

---

## 문서 구성 (Documentation)

| 문서 | 내용 |
|:---|:---|
| [docs/CURRICULUM.md](docs/CURRICULUM.md) | 전체 커리큘럼, 챕터별 선수학습과 예제 범위 |
| [docs/TECH-STACK.md](docs/TECH-STACK.md) | 확정 라이브러리 버전과 Spring Boot 4 변경 사항 |
| `docs/chapters/chNN/` | 챕터별 학습 목표와 범위 |

---

## 문제 해결 (Troubleshooting)

### Q. `./gradlew` 실행 시 권한 오류(Permission Denied)가 발생합니다.
실행 권한이 누락된 경우입니다. 터미널에서 아래 명령어로 실행 권한을 부여하십시오:
```bash
chmod +x gradlew
```

### Q. `Cannot find a Java installation on your machine ... matching: {languageVersion=21 ...}` 오류가 발생합니다.
JDK 21이 설치되어 있지 않을 때 발생합니다(JDK 17로 실행해도 같은 메시지가 나옵니다). `java -version` 을 확인하고 OpenJDK 21을 설치한 후 `JAVA_HOME` 환경변수를 설정하십시오.

### Q. `Web server failed to start. Port 8080 was already in use.` 오류가 발생합니다.
다른 프로세스가 8080 포트를 사용 중입니다. 해당 프로세스를 종료하거나 JAR 실행 시 `--server.port=8081` 로 포트를 바꾸십시오.

### Q. Gradle Daemon 관련 연결/소켓 오류가 발생합니다.
백그라운드 데몬 프로세스 충돌이나 방화벽이나 보안 프로그램의 로컬호스트 연결 제한일 수 있습니다. `--no-daemon` 플래그를 붙여 단독 프로세스로 실행하십시오:
```bash
./gradlew --no-daemon build
```

### Q. 예전 Spring Boot 3 자료의 테스트 코드를 복사했더니 컴파일이 안 됩니다.
Spring Boot 4에서는 웹 스타터 이름(`spring-boot-starter-webmvc`)과 `@AutoConfigureMockMvc`, `@WebMvcTest` 의 패키지(`org.springframework.boot.webmvc.test.autoconfigure`)가 바뀌었습니다. 자세한 내용은 [docs/TECH-STACK.md](docs/TECH-STACK.md)의 "Boot 4에서 달라진 점"을 참고하십시오.

---

## 라이선스 (License)

[MIT License](LICENSE)

---

## 관련 링크 (References)

- **공식 온라인 커리큘럼**: [jqdom.com Spring Boot Curriculum](https://jqdom.com/blog?categoryId=46)
- **저장소 이슈 등록**: [GitHub Issues](https://github.com/devguna81/spring-boot-curriculum/issues)
- **Java 기초가 필요하다면**: [Java Learning 예제 저장소](https://github.com/devguna81/java-learning)
