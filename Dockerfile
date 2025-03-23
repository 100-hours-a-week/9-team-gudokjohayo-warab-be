# 1단계: 빌드
FROM gradle:8.5-jdk17-alpine AS builder

WORKDIR /app
COPY --chown=gradle:gradle . /app

# 캐싱을 위해 dependencies 먼저 빌드
RUN gradle build -x test --no-daemon

# 2단계: 실행 이미지
FROM eclipse-temurin:17-jdk-alpine

WORKDIR /app

# 위 빌더에서 JAR 복사
COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
