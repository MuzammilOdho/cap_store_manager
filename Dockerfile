# Use JDK 17 for building
FROM eclipse-temurin:17-jdk AS builder
WORKDIR /app

# Copy Maven wrapper + pom
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

# Download dependencies
RUN ./mvnw dependency:go-offline -B

# Copy source code
COPY src src

# Build app
RUN ./mvnw package -DskipTests

# --------------------
# Run stage
FROM eclipse-temurin:17-jdk
WORKDIR /app

# Copy built JAR
COPY --from=builder /app/target/store-1.0.0.jar app.jar

# Run app with docker profile
ENTRYPOINT ["java","-jar","-Dspring.profiles.active=docker","app.jar"]
