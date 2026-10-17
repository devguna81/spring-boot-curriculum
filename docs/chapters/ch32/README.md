# ch32. Docker

## 학습 목표

- 멀티 스테이지 Dockerfile로 빌드 도구(JDK, Gradle)는 이미지에 남기지 않고 JRE만 담는다.
- Spring Boot의 계층형 JAR 추출로 의존성 층과 애플리케이션 층을 나눠 이미지 캐시를 활용한다.
- root가 아닌 사용자로 실행하고, `HEALTHCHECK`를 둔다.
- `docker compose`로 애플리케이션과 PostgreSQL을 함께 띄우고, `depends_on`의 `service_healthy` 조건으로 시작 순서를 맞춘다.
- 환경 변수로 설정을 덮어쓰고(ch02), 비밀번호는 파일에 적지 않는다.

## 선수학습

ch01

## 범위

| 항목 | 내용 |
|---|---|
| 예제 | [`examples/ch32-docker`](../../../examples/ch32-docker) (`Dockerfile`, `docker-compose.yml`) |
| 애플리케이션 | `GET /hello`, `GET /visits`(DB의 방문 수 증가), `/actuator/health` |
| 테스트 | Gradle 테스트 3건(H2). 컨테이너 동작은 아래 명령으로 확인 |
| 다루지 않는 것 | 이미지 레지스트리 푸시, 쿠버네티스, 이미지 취약점 스캔, 멀티 아키텍처 빌드 |

## 실행

빌드 컨텍스트는 저장소 루트입니다. `DB_PASSWORD`는 실행할 때 직접 정합니다(없으면 시작하지 않습니다).

```bash
DB_PASSWORD=원하는값 docker compose -f examples/ch32-docker/docker-compose.yml up --build
# 다른 터미널에서
curl http://localhost:8080/hello
curl http://localhost:8080/visits
docker compose -f examples/ch32-docker/docker-compose.yml down        # 데이터 볼륨은 남는다
docker compose -f examples/ch32-docker/docker-compose.yml down -v     # 볼륨까지 삭제
```

## 다음 챕터

ch33. GitHub Actions와 CI/CD
