# syntax=docker/dockerfile:1
# 빌드 컨텍스트는 저장소 루트다: docker build -f projects/final-project/Dockerfile -t final-project .

# ---- 1단계: React 화면 빌드 ----
FROM node:24-bookworm-slim AS frontend
WORKDIR /f
# 의존성 파일만 먼저 복사해 소스가 바뀌어도 npm ci 층이 캐시되게 한다.
COPY projects/final-project/frontend/package.json projects/final-project/frontend/package-lock.json ./
RUN npm ci
COPY projects/final-project/frontend/ ./
RUN npm run build

# ---- 2단계: 서버 빌드 (JDK, Gradle) ----
FROM eclipse-temurin:21-jdk-jammy AS build
WORKDIR /w
COPY . .
# React 빌드 결과를 정적 파일 위치에 넣어 한 JAR로 묶는다. /app/ 으로 제공된다.
COPY --from=frontend /f/dist projects/final-project/src/main/resources/static/app
# 테스트는 CI에서 이미 실행했다. 이미지 빌드는 패키징만 한다.
RUN ./gradlew :projects:final-project:bootJar --no-daemon --no-build-cache -x test \
 && mkdir /extracted \
 && java -Djarmode=tools -jar projects/final-project/build/libs/final-project-0.0.1-SNAPSHOT.jar extract --layers --destination /extracted

# ---- 3단계: 실행 (JRE만) ----
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

# root가 아닌 사용자로 실행한다.
RUN useradd --system --uid 10001 --no-create-home app
USER app

# 자주 바뀌는 층(application)을 마지막에 복사해 의존성 층이 캐시되게 한다.
COPY --from=build /extracted/dependencies/ ./
COPY --from=build /extracted/spring-boot-loader/ ./
COPY --from=build /extracted/snapshot-dependencies/ ./
COPY --from=build /extracted/application/ ./

EXPOSE 8080

# JRE 이미지에는 curl이 없어서 bash의 /dev/tcp로 헬스 엔드포인트를 확인한다.
HEALTHCHECK --interval=10s --timeout=3s --start-period=40s --retries=3 \
  CMD bash -c 'exec 3<>/dev/tcp/127.0.0.1/8080 && printf "GET /actuator/health/liveness HTTP/1.0\r\n\r\n" >&3 && grep -q "\"UP\"" <&3' || exit 1

ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75", "-jar", "final-project-0.0.1-SNAPSHOT.jar"]
