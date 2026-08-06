# === Stage 1: App build ===
FROM gradle:9.6-jdk21-alpine AS tl-builder
WORKDIR /app

# Copy config files
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .

# Copy Src
COPY lib-srd-api/src lib-srd-api/src
COPY lib-srd-api/build.gradle lib-srd-api/.

COPY app-teya-ledger/src app-teya-ledger/src
COPY app-teya-ledger/build.gradle app-teya-ledger/.

# build production-ready ja (skip tests)
RUN gradle bootJar -x test --no-daemon

# === Stage 2: Lightweight runtime image ===
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Copy ony jar-file from build stage
COPY --from=tl-builder /app/app-teya-ledger/build/libs/*.jar app.jar

EXPOSE 8080

# run...
ENTRYPOINT ["java", "-jar", "-XX:+UseG1GC", "app.jar"]