FROM gradle:8.10.2-jdk17 AS builder
WORKDIR /app
COPY build.gradle settings.gradle gradle.properties ./
COPY gradle ./gradle
RUN gradle dependencies --no-daemon > /dev/null 2>&1 || true
COPY src ./src
RUN gradle bootJar -x test --no-daemon

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=builder /app/build/libs/*.jar app.jar
EXPOSE 8080
CMD ["sh", "-c", "java -XX:+UseSerialGC -XX:MaxRAMPercentage=75 -Djava.security.egd=file:/dev/./urandom -Dserver.port=${PORT:-8080} -Dspring.main.lazy-initialization=true -Dspring.jmx.enabled=false -Dspring.main.banner-mode=off -jar /app/app.jar"]