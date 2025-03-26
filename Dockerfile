# 빌드 스테이지 - 멀티플랫폼 지원 이미지 사용
FROM gradle:7-jdk17 AS build
WORKDIR /app

# 메모리 설정 및 Gradle 최적화
ENV GRADLE_OPTS="-Dorg.gradle.jvmargs=-Xmx4g -XX:MaxMetaspaceSize=1g -Dorg.gradle.parallel=true"

# 의존성 캐싱을 위해 build.gradle 먼저 복사
COPY build.gradle settings.gradle ./
COPY gradle ./gradle
RUN gradle dependencies --no-daemon || true

# 소스 코드 복사 및 빌드
COPY src ./src
RUN gradle assemble --no-daemon -x test -x setGitCommitTemplate -x copyGitHooks

# 실행 이미지 - 멀티플랫폼 지원 이미지 사용
FROM amazoncorretto:17-alpine
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
