# Giai đoạn 1: Build
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app

# Cache dependencies trước
COPY pom.xml .
RUN mvn dependency:go-offline

# Sau đó mới copy code và build
COPY src ./src
RUN mvn clean package -DskipTests

# Giai đoạn 2: Chạy
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

COPY --from=build /app/target/group-10-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-XX:MaxRAMPercentage=80.0", "-XX:InitialRAMPercentage=50.0", "-jar", "app.jar"]