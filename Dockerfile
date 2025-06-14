# Stage 1: Build
# Sử dụng Maven với JDK 17 để build ứng dụng
FROM maven:3.9.6-eclipse-temurin-17 AS build

# Tạo thư mục làm việc và sao chép mã nguồn
WORKDIR /app
COPY pom.xml .
COPY src ./src

# Build ứng dụng và bỏ qua test
RUN mvn package -DskipTests

# Stage 2: Tạo image chạy ứng dụng
# Sử dụng JDK 17 nhẹ
FROM openjdk:17-jdk-slim

# Thiết lập thư mục làm việc
WORKDIR /app

# Sao chép file JAR từ stage build sang
COPY --from=build /app/target/*.jar app.jar

# Mở port ứng dụng (sửa lại nếu không phải 8080)
EXPOSE 8081

# Lệnh chạy ứng dụng
ENTRYPOINT ["java", "-jar", "app.jar"]
