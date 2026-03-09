# Giai đoạn 1: Build
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Giai đoạn 2: Chạy
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
# Kiểm tra kỹ tên file jar này có khớp với pom.xml không
COPY --from=build /app/target/group-10-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
# Giới hạn RAM để tránh lỗi Out of Memory trên Render Free
ENTRYPOINT ["java", "-Xmx300m", "-jar", "app.jar"]