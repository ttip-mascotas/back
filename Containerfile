FROM gradle:8.6.0-jdk21-alpine as builder

WORKDIR /artifact

COPY build.gradle.kts settings.gradle.kts gradlew ./
COPY src src

RUN gradle build -x test --no-daemon --stacktrace


FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

COPY --from=builder /artifact/build/libs/history*.jar history.jar
COPY --from=builder /artifact/build/resources/ resources/
CMD ["java", "-jar", "/app/history.jar"]
