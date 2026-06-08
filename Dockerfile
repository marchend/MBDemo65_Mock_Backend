# Multi-stage container build for mbdemo65-mock-backend
# (gradle / Java 21). Build with the
# JDK, ship on a slim JRE so the runtime image stays small.

# ── build ──────────────────────────────────────────────
FROM eclipse-temurin:21-jdk AS build
WORKDIR /app
COPY . .
RUN ./gradlew --no-daemon clean bootJar

# ── run ────────────────────────────────────────────────
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
