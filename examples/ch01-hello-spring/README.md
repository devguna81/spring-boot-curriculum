# ch01. Hello Spring Boot

Spring Boot 프로젝트를 만들고 첫 번째 REST API(`GET /hello`)를 실행해 봅니다.

## 실행 환경

- Java 21
- Spring Boot 4.1.1
- Gradle (Wrapper 포함, 별도 설치 불필요)

## 프로젝트 구조

```text
ch01-hello-spring-boot/
├── build.gradle
├── settings.gradle
├── gradlew / gradlew.bat
└── src
    ├── main
    │   ├── java/com/devguna/curriculum/ch01
    │   │   ├── Ch01HelloSpringBootApplication.java   # 애플리케이션 시작점
    │   │   └── HelloController.java                  # GET /hello
    │   └── resources/application.properties
    └── test/java/com/devguna/curriculum/ch01
        ├── Ch01HelloSpringBootApplicationTests.java  # 컨텍스트 로딩 테스트
        └── HelloControllerTests.java                 # API 응답 테스트
```

## 실행

```bash
cd ch01-hello-spring-boot
./gradlew bootRun
```

Windows에서는 `gradlew.bat bootRun`을 사용합니다.

## 확인

```bash
curl http://localhost:8080/hello
# Hello, Spring Boot!

curl "http://localhost:8080/hello?name=devguna"
# Hello, devguna!
```

브라우저에서 http://localhost:8080/hello 를 열어도 됩니다.

## 테스트

```bash
./gradlew test
```
