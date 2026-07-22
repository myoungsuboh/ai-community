# syntax=docker/dockerfile:1
# 파라미터화된 백엔드 이미지: docker build --build-arg SERVICE=auth-service ...
# 규칙: skills/devops/container-docker-containerization.md (멀티스테이지·non-root·시크릿 런타임 주입)

# ---- build ----
FROM gradle:8.10.2-jdk17 AS build
WORKDIR /workspace
COPY . .
ARG SERVICE
RUN gradle :${SERVICE}:bootJar --no-daemon -x test

# ---- runtime ----
FROM eclipse-temurin:17-jre AS runtime
ARG SERVICE
WORKDIR /app
# non-root 전용 사용자
RUN groupadd --system app && useradd --system --gid app --no-create-home app
COPY --from=build /workspace/${SERVICE}/build/libs/${SERVICE}-0.0.1.jar /app/app.jar
USER app
# 모든 서비스 컨테이너는 내부적으로 8080 을 사용(운영 프로필). 시크릿은 런타임 env 로 주입.
ENV SERVER_PORT=8080 \
    SPRING_PROFILES_ACTIVE=docker
EXPOSE 8080
ENTRYPOINT ["sh","-c","java ${JAVA_OPTS:-} -jar /app/app.jar"]
