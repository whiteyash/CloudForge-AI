FROM maven:3.9.9-eclipse-temurin-21 AS builder

WORKDIR /app

COPY pom.xml ./
RUN mvn --batch-mode --no-transfer-progress dependency:go-offline

COPY src ./src
RUN mvn --batch-mode --no-transfer-progress package -DskipTests

FROM eclipse-temurin:21-jre-alpine AS runtime

WORKDIR /app

RUN addgroup --system --gid 1001 cloudforge \
    && adduser --system --uid 1001 --ingroup cloudforge cloudforge

COPY --from=builder --chown=cloudforge:cloudforge /app/target/cloudforge-api-*.jar app.jar

USER cloudforge

EXPOSE 8000

ENTRYPOINT ["java", "-jar", "app.jar"]
