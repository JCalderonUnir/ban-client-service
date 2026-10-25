# syntax=docker/dockerfile:1.7
# =========================
# STAGE 1: Build
# =========================
FROM eclipse-temurin:21-jdk AS build

WORKDIR /app

COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

RUN chmod +x mvnw
COPY src ./src

RUN --mount=type=cache,id=fincore-maven,target=/root/.m2 ./mvnw clean package -Dmaven.test.skip=true

# =========================
# STAGE 2: Runtime
# =========================
FROM eclipse-temurin:21-jre

WORKDIR /app

RUN groupadd --system app && useradd --system --uid 10001 --gid app app
COPY --from=build --chown=app:app /app/target/*.jar app.jar
USER 10001:app

EXPOSE 8000

ENTRYPOINT ["java", "-jar", "app.jar"]
