# syntax=docker/dockerfile:1
# 빌드 컨텍스트는 저장소 루트다: docker build -f examples/ch32-docker/Dockerfile -t ch32-docker .

# ---- 1단계: 빌드 (JDK, Gradle) ----
FROM eclipse-temurin:21-jdk-jammy AS build
WORKDIR /w
COPY . .
RUN ./gradlew :examples:ch32-docker:bootJar --no-daemon --no-build-cache -x test \
 && mkdir /extracted \
 && java -Djarmode=tools -jar examples/ch32-docker/build/libs/ch32-docker-0.0.1-SNAPSHOT.jar extract --layers --destination /extracted

# ---- 2단계: 실행 (JRE만) ----
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
HEALTHCHECK --interval=10s --timeout=3s --start-period=30s --retries=3 \
  CMD bash -c 'exec 3<>/dev/tcp/127.0.0.1/8080 && printf "GET /actuator/health/liveness HTTP/1.0\r\n\r\n" >&3 && grep -q "\"UP\"" <&3' || exit 1

ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75", "-jar", "ch32-docker-0.0.1-SNAPSHOT.jar"]
