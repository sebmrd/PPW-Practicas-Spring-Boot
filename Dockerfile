# ETAPA 1: BUILD
FROM gradle:8.5-jdk17 AS builder
WORKDIR /build
COPY build.gradle.kts settings.gradle.kts ./
COPY gradle ./gradle
RUN gradle dependencies --no-daemon
COPY src ./src
RUN gradle build -x test --no-daemon

# ETAPA 2: RUNTIME
FROM eclipse-temurin:17-jre-alpine
RUN addgroup -S spring && adduser -S spring -G spring
WORKDIR /app
COPY --from=builder /build/build/libs/*.jar app.jar
RUN chown spring:spring app.jar
USER spring:spring
EXPOSE 8080
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health
ENV SPRING_PROFILES_ACTIVE=prod
ENTRYPOINT ["java", \
  "-Djava.security.egd=file:/dev/./urandom", \
  "-Xms256m", \
  "-Xmx512m", \
  "-jar", \
  "app.jar"]
