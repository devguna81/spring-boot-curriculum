# Spring Boot Curriculum

[PlaygroundDev 블로그](https://jqdom.com/blog?categoryId=46)의 **Spring Boot 4 실습형 커리큘럼** 연재에 쓰는 예제 코드 저장소입니다.
챕터마다 실행 가능한 예제가 있고, 블로그 글은 이 코드를 기준으로 작성합니다.

## 실행 환경

- JDK 21
- Spring Boot 4.1.1
- Gradle (Wrapper 포함, 별도 설치 불필요)

확정 버전과 근거는 [docs/TECH-STACK.md](docs/TECH-STACK.md)를 참고하세요.

## 빠른 시작

```bash
git clone https://github.com/devguna81/spring-boot-curriculum.git
cd spring-boot-curriculum

# Linux / macOS
./gradlew :examples:ch01-hello-spring:bootRun

# Windows
gradlew.bat :examples:ch01-hello-spring:bootRun
```

전체 빌드와 테스트: `./gradlew clean build` (Windows: `gradlew.bat clean build`)

## 목차


| Part | 주제 | 챕터 |
|---|---|---|
| 01 | Spring Boot 입문 | [ch01 Hello Spring Boot](examples/ch01-hello-spring), ch02 설정과 Profile, ch03 IoC/DI/Bean |
| 02 | Spring MVC 및 REST API | ch04~ch07 |
| 03 | Database 및 MyBatis | ch08~ch14 |
| 04 | MapStruct 및 API 명세 | ch15~ch17 |
| 05 | 화면 개발 (Thymeleaf, React) | ch18~ch21 |
| 06 | 실무 아키텍처 | ch22~ch25 |
| 07 | 테스트 및 운영 | ch26~ch32 |
| 08 | 종합 프로젝트 | `projects/final-project` |

링크가 걸린 챕터만 구현되어 있습니다.

## 저장소 구조

```text
spring-boot-curriculum/
├── examples/        챕터별 예제 (Gradle 멀티 프로젝트)
├── projects/        종합 프로젝트
├── docs/            커리큘럼, 기술 스택, 챕터별 문서와 블로그 초안
└── .github/         GitHub Actions
```

## 라이선스

[MIT](LICENSE)
