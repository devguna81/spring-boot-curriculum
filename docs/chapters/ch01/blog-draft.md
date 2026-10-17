<!--
검토자 메모 (게시 시 이 주석은 제외)
- 상태: DRAFT_READY. 사용자 승인(REVIEWED) 전에는 게시하지 않는다.
- 코드 링크의 main 경로는 PR 머지 후에 열린다. 머지 전에는 하단의 SHA 고정 링크로 확인할 수 있다.
- 본문의 모든 출력은 2026-10-17 실행 결과를 그대로 옮긴 것이다. (verification.md 참조)
-->

# Spring Boot 4 시작하기 (1): 프로젝트 만들고 첫 API 실행하기

## 학습 목표

- Spring Framework와 Spring Boot가 각각 무엇을 맡는지 설명할 수 있다.
- Gradle 기반 Spring Boot 프로젝트의 구조를 읽을 수 있다.
- 애플리케이션을 실행하고 `GET /hello`, `GET /info` 응답을 확인한다.
- MockMvc로 컨트롤러를 테스트한다.

## 선수학습

Java 기본 문법(클래스, 메서드, record)을 알고 있으면 충분합니다. Spring 경험은 필요 없습니다.

## 핵심 개념

### Spring Framework와 Spring Boot

**Spring Framework**는 객체를 만들고 연결해 주는 컨테이너(IoC, DI)와 웹, 데이터 접근, 테스트 같은 기능 모음입니다. 강력하지만 예전에는 설정할 것이 많았습니다. 어떤 라이브러리를 어떤 버전으로 묶을지, 서버는 어떻게 띄울지를 직접 정해야 했습니다.

**Spring Boot**는 이 설정 부담을 줄여 줍니다.

| 문제 | Boot의 해법 |
|---|---|
| 라이브러리 버전 조합을 직접 맞춰야 함 | 스타터(starter)와 BOM이 검증된 조합을 제공 |
| 설정 파일이 길어짐 | 클래스패스를 보고 자동 구성(auto-configuration) |
| WAR로 만들어 서버에 배포 | 내장 Tomcat이 포함된 실행 가능한 JAR |

이 글에서 쓰는 버전은 Spring Boot 4.1.1, Java 21입니다. Boot 4는 Spring Framework 7 기반(이 예제는 7.0.9)이고, 내장 서버는 Tomcat 11입니다. 실행 로그에서 `Apache Tomcat/11.0.24`를 확인할 수 있습니다.

### Boot 3에서 넘어올 때 달라진 점

예전 글의 코드를 그대로 가져오면 컴파일되지 않는 부분이 있습니다. 이 예제에서 부딪히는 두 가지입니다.

| 항목 | Boot 3.x | Boot 4.x |
|---|---|---|
| 웹 스타터 | `spring-boot-starter-web` | `spring-boot-starter-webmvc` |
| `@AutoConfigureMockMvc` 패키지 | `org.springframework.boot.test.autoconfigure.web.servlet` | `org.springframework.boot.webmvc.test.autoconfigure` |

## 동작 원리

요청이 들어오면 아래 순서로 처리됩니다.

```text
HTTP 요청
  -> 내장 Tomcat
  -> DispatcherServlet     (요청을 어느 컨트롤러가 처리할지 결정)
  -> HelloController       (@GetMapping 메서드 실행)
  -> 반환값을 응답 본문으로 변환 (String은 그대로, 객체는 JSON)
  -> HTTP 응답
```

`main` 메서드가 `SpringApplication.run(...)`을 호출하면 다음이 일어납니다.

1. `@SpringBootApplication`이 붙은 클래스의 패키지 아래를 스캔해 `@RestController` 같은 Bean 후보를 찾습니다.
2. 클래스패스에 웹 스타터가 있으므로 Tomcat과 DispatcherServlet을 자동 구성합니다.
3. 8080 포트로 서버를 시작합니다.

## 프로젝트 구조

```text
ch01-hello-spring/
├── build.gradle
└── src
    ├── main
    │   ├── java/com/devguna/curriculum/ch01
    │   │   ├── Ch01Application.java    # 시작점
    │   │   ├── HelloController.java    # GET /hello, GET /info
    │   │   └── AppInfo.java            # /info 응답 record
    │   └── resources/application.yml
    └── test/java/com/devguna/curriculum/ch01
        ├── Ch01ApplicationTests.java
        └── HelloControllerTests.java
```

`src/main/java`에는 실제 코드, `src/main/resources`에는 설정 파일, `src/test`에는 테스트가 들어갑니다. 패키지 `com.devguna.curriculum.ch01`이 스캔의 기준점이므로 모든 클래스는 이 아래에 둡니다.

## 예제 코드 설명

### build.gradle

```groovy
plugins {
    id 'java'
    id 'org.springframework.boot'
    id 'io.spring.dependency-management'
}

dependencies {
    implementation 'org.springframework.boot:spring-boot-starter-webmvc'

    testImplementation 'org.springframework.boot:spring-boot-starter-webmvc-test'
    testRuntimeOnly 'org.junit.platform:junit-platform-launcher'
}
```

- `org.springframework.boot` 플러그인은 실행 가능한 JAR 빌드(`bootJar`)와 실행(`bootRun`)을 제공합니다.
- `dependency-management` 플러그인은 Boot가 검증한 버전을 적용해서, 의존성에 버전을 적지 않아도 됩니다.
- 이 저장소는 여러 챕터를 한 번에 빌드하려고 Boot 버전을 루트 `gradle.properties`에서 관리합니다. 단독 프로젝트라면 plugins 블록에 `version '4.1.1'`을 직접 적으면 됩니다.

### Ch01Application.java

```java
@SpringBootApplication
public class Ch01Application {

    public static void main(String[] args) {
        SpringApplication.run(Ch01Application.class, args);
    }
}
```

`@SpringBootApplication` 하나가 설정 클래스 선언, 자동 구성 활성화, 컴포넌트 스캔 세 가지를 묶은 애노테이션입니다.

### HelloController.java

```java
@RestController
public class HelloController {

    private final String applicationName;

    public HelloController(@Value("${spring.application.name}") String applicationName) {
        this.applicationName = applicationName;
    }

    @GetMapping("/hello")
    public String hello(@RequestParam(defaultValue = "Spring Boot") String name) {
        return "Hello, " + name + "!";
    }

    @GetMapping("/info")
    public AppInfo info() {
        return new AppInfo(
                applicationName,
                System.getProperty("java.version"),
                SpringBootVersion.getVersion());
    }
}
```

- `@RestController`는 반환값을 View 이름이 아니라 응답 본문으로 씁니다.
- `@GetMapping("/hello")`는 `GET /hello` 요청을 이 메서드에 연결합니다.
- `@RequestParam(defaultValue = ...)`는 쿼리 문자열 `?name=`을 받고, 없으면 기본값을 씁니다.
- `/info`는 문자열이 아니라 record를 반환합니다. Jackson이 필드 이름을 키로 하는 JSON으로 바꿔 줍니다.
- 생성자의 `@Value`는 `application.yml` 값을 주입받습니다. 생성자로 값을 받는 방식은 3장(DI)에서 자세히 다룹니다.

### AppInfo.java, application.yml

```java
public record AppInfo(String name, String javaVersion, String springBootVersion) {
}
```

```yaml
spring:
  application:
    name: ch01-hello-spring
```

`record`는 불변 데이터 객체를 한 줄로 선언합니다. 설정 파일 사용법은 2장에서 다룹니다.

### 테스트: HelloControllerTests.java

```java
@SpringBootTest
@AutoConfigureMockMvc
class HelloControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void helloReturnsDefaultGreeting() throws Exception {
        mockMvc.perform(get("/hello"))
                .andExpect(status().isOk())
                .andExpect(content().string("Hello, Spring Boot!"));
    }

    @Test
    void infoReturnsJson() throws Exception {
        mockMvc.perform(get("/info"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("ch01-hello-spring"))
                .andExpect(jsonPath("$.javaVersion", startsWith("21")))
                .andExpect(jsonPath("$.springBootVersion", startsWith("4.")));
    }
}
```

`MockMvc`는 실제 서버를 띄우지 않고 DispatcherServlet에 요청을 흘려보내 응답을 검사합니다. 전체 파일에는 `name` 파라미터 케이스까지 포함해 3개 테스트가 있고, 컨텍스트 로딩 테스트 1개를 더해 총 4개입니다.

## 실행 방법

JDK 21이 필요합니다. Gradle은 저장소에 포함된 Wrapper를 씁니다. 저장소 루트에서 실행합니다.

```bash
git clone https://github.com/devguna81/spring-boot-curriculum.git
cd spring-boot-curriculum
```

| OS | 실행 | 이 챕터만 빌드 | 전체 빌드 |
|---|---|---|---|
| Linux / macOS | `./gradlew :examples:ch01-hello-spring:bootRun` | `./gradlew :examples:ch01-hello-spring:build` | `./gradlew clean build` |
| Windows | `gradlew.bat :examples:ch01-hello-spring:bootRun` | `gradlew.bat :examples:ch01-hello-spring:build` | `gradlew.bat clean build` |

테스트 결과(`./gradlew --no-daemon clean build`):

```text
Ch01ApplicationTests > contextLoads() PASSED
HelloControllerTests > helloReturnsGreetingWithName() PASSED
HelloControllerTests > helloReturnsDefaultGreeting() PASSED
HelloControllerTests > infoReturnsJson() PASSED

BUILD SUCCESSFUL
```

## API 실행 결과

실행 로그의 핵심 줄입니다.

```text
Starting Ch01Application v0.0.1-SNAPSHOT using Java 21.0.11
No active profile set, falling back to 1 default profile: "default"
Starting Servlet engine: [Apache Tomcat/11.0.24]
Tomcat started on port 8080 (http) with context path '/'
Started Ch01Application in 3.737 seconds
```

```bash
curl http://localhost:8080/hello
```
```text
Hello, Spring Boot!
```

```bash
curl "http://localhost:8080/hello?name=devguna"
```
```text
Hello, devguna!
```

```bash
curl http://localhost:8080/info
```
```json
{"name":"ch01-hello-spring","javaVersion":"21.0.11","springBootVersion":"4.1.1"}
```

존재하지 않는 경로를 호출하면 Boot가 JSON 오류를 돌려줍니다.

```bash
curl http://localhost:8080/nope
```
```json
{"timestamp":"2026-10-17T02:17:25.343Z","status":404,"error":"Not Found","path":"/nope"}
```

## 자주 발생하는 오류

| 증상 | 원인 | 해결 |
|---|---|---|
| `Port 8080 was already in use` | 다른 프로세스가 8080을 사용 중 | 해당 프로세스를 종료하거나 `--server.port=8081`로 실행 |
| `Cannot find a Java installation on your machine ... matching: {languageVersion=21 ...}` | JDK 21이 설치되어 있지 않음 (JDK 17로 실행하면 이 메시지가 나옴) | JDK 21 설치 후 `java -version` 확인 |
| `package org.springframework.boot.test.autoconfigure.web.servlet does not exist` | Boot 3 시절 import를 그대로 사용 | `org.springframework.boot.webmvc.test.autoconfigure`로 변경, 테스트 스타터는 `spring-boot-starter-webmvc-test` |
| 컨트롤러가 404 | 컨트롤러가 `@SpringBootApplication` 클래스의 패키지 밖에 있음 | 시작 클래스와 같은 패키지 또는 하위로 이동 |
| `Could not resolve placeholder 'spring.application.name'` | `application.yml`이 없거나 키가 다름 | `src/main/resources/application.yml` 확인 |

## GitHub 예제 링크

| 대상 | 링크 |
|---|---|
| 챕터 전체 | https://github.com/devguna81/spring-boot-curriculum/tree/main/examples/ch01-hello-spring |
| 시작 클래스 | https://github.com/devguna81/spring-boot-curriculum/blob/main/examples/ch01-hello-spring/src/main/java/com/devguna/curriculum/ch01/Ch01Application.java |
| 컨트롤러 | https://github.com/devguna81/spring-boot-curriculum/blob/main/examples/ch01-hello-spring/src/main/java/com/devguna/curriculum/ch01/HelloController.java |
| 설정 파일 | https://github.com/devguna81/spring-boot-curriculum/blob/main/examples/ch01-hello-spring/src/main/resources/application.yml |
| 테스트 | https://github.com/devguna81/spring-boot-curriculum/blob/main/examples/ch01-hello-spring/src/test/java/com/devguna/curriculum/ch01/HelloControllerTests.java |
| 빌드 파일 | https://github.com/devguna81/spring-boot-curriculum/blob/main/examples/ch01-hello-spring/build.gradle |
| 챕터 README | https://github.com/devguna81/spring-boot-curriculum/blob/main/examples/ch01-hello-spring/README.md |

이 글은 커밋 `61a3bb118238a2f9e8632802c81844f48e9018cc` 기준으로 검증했습니다. 이 시점의 코드는 아래 링크로 고정해서 볼 수 있습니다.

https://github.com/devguna81/spring-boot-curriculum/tree/61a3bb118238a2f9e8632802c81844f48e9018cc/examples/ch01-hello-spring

## 핵심 정리

- Spring Framework는 객체 관리와 기능 모음이고, Spring Boot는 스타터, 자동 구성, 내장 서버로 설정 부담을 줄여 줍니다.
- `@SpringBootApplication` 클래스의 패키지가 컴포넌트 스캔의 기준입니다.
- `@RestController` + `@GetMapping`으로 API를 만들고, 문자열은 그대로, 객체는 JSON으로 응답합니다.
- Boot 4에서는 웹 스타터 이름(`spring-boot-starter-webmvc`)과 테스트 애노테이션 패키지가 바뀌었습니다.
- MockMvc로 서버를 띄우지 않고 요청/응답을 검증할 수 있습니다.

## 다음 학습 안내

다음 글에서는 `application.yml`로 설정을 관리하고, Profile로 개발/운영 환경을 나누는 방법을 다룹니다. (ch02. 설정과 Profile)
