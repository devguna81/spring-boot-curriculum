# ch01 검증 기록

검증일: 2026-10-17
검증 대상 커밋: `61a3bb118238a2f9e8632802c81844f48e9018cc` (브랜치 `feature/foundation-and-ch01`)

## 환경

| 항목 | 값 |
|---|---|
| 실행 방식 | Docker 컨테이너 (`eclipse-temurin:21.0.11_10-jdk-jammy`) |
| 사유 | 개발 PC의 샌드박스에서 localhost 연결이 막혀 Gradle 데몬이 기동하지 못함. 컨테이너 안에서는 정상 |
| Java | 21.0.11 |
| Gradle | 9.8.1 (Wrapper, `--no-daemon`) |
| Spring Boot | 4.1.1 (Spring Framework 7.0.9, Tomcat 11.0.24) |

## 결과

| # | 항목 | 명령 | 결과 |
|---|---|---|---|
| 1 | 전체 빌드 | `./gradlew --no-daemon clean build` | BUILD SUCCESSFUL |
| 2 | 챕터 단독 빌드 | `./gradlew --no-daemon :examples:ch01-hello-spring:build` | 성공 (exit 0) |
| 3 | 단위/통합 테스트 | (1에 포함) | 4건 중 4건 통과 |
| 4 | `bootRun` 실행 | `./gradlew --no-daemon :examples:ch01-hello-spring:bootRun` | 기동, `GET /hello` 응답 확인 |
| 5 | JAR 실행과 API 호출 | `java -jar ch01-hello-spring-0.0.1-SNAPSHOT.jar` | 4개 요청 모두 기대 응답 |
| 6 | 오류 메시지 재현 | 포트 중복, 설정 파일 누락, JDK 17 실행 | 블로그 초안의 문구와 일치 |

### 테스트 상세

```text
Ch01ApplicationTests > contextLoads() PASSED
HelloControllerTests > helloReturnsGreetingWithName() PASSED
HelloControllerTests > helloReturnsDefaultGreeting() PASSED
HelloControllerTests > infoReturnsJson() PASSED
```

### API 실제 응답 (JAR 실행)

| 요청 | 상태 | 본문 |
|---|---|---|
| `GET /hello` | 200 | `Hello, Spring Boot!` |
| `GET /hello?name=devguna` | 200 | `Hello, devguna!` |
| `GET /info` | 200 | `{"name":"ch01-hello-spring","javaVersion":"21.0.11","springBootVersion":"4.1.1"}` |
| `GET /nope` | 404 | `{"timestamp":"2026-10-17T02:17:25.343Z","status":404,"error":"Not Found","path":"/nope"}` |

### 오류 재현

| 상황 | 실제 출력 |
|---|---|
| 8080 포트 사용 중 | `Web server failed to start. Port 8080 was already in use.` |
| `application.yml` 없음 | `PlaceholderResolutionException: Could not resolve placeholder 'spring.application.name' in value "${spring.application.name}"` |
| JDK 17로 빌드 | `Cannot find a Java installation on your machine ... matching: {languageVersion=21 ...}` |

## 미검증 항목

| 항목 | 사유 |
|---|---|
| Windows (`gradlew.bat`) 실행 | Windows 환경이 없어 실행하지 못했다. 명령은 문서에 적었지만 검증하지 않았다 |
| 호스트 JDK 21에서 직접 실행 | 위 환경 제약으로 컨테이너에서만 실행했다 |
| Oracle | 이 챕터는 DB를 사용하지 않는다 (해당 없음) |

## CI

| 항목 | 값 |
|---|---|
| 워크플로 | `build` (PR #1, `pull_request`) |
| 실행 | https://github.com/devguna81/spring-boot-curriculum/actions/runs/38996455608 |
| 대상 커밋 | `654d794dc1a3740893f0a20c9b618f47d88add2f` (PR head) |
| 환경 | ubuntu-latest, Temurin 21.0.12, Gradle 9.8.1 |
| 결과 | 성공 (48초) |
| 테스트 | 로그에서 4건 모두 `PASSED` 확인 (초록불만 보고 판단하지 않고 로그를 직접 확인) |

예제 코드는 검증 대상 커밋 `61a3bb1` 이후 변경이 없다. 이후 커밋은 문서만 바꿨다.

CI 경고(실패 아님): `actions/setup-java@v4` 등이 Node.js 20 기반이라 deprecated 안내가 나온다. 최신은 setup-java v6, checkout v7이며, 별도 정비 대상으로 남긴다.
