# --- STAGE 1: BUILDER ---
# Use a JDK image (with Maven/Gradle) to compile the source code
FROM maven:3.9.6-eclipse-temurin-21-slim AS builder
WORKDIR /app

# 1. Copy the pom.xml and source code
# This step allows caching of dependencies
COPY pom.xml .
COPY src /app/src

# 2. Build the JAR file
# The 'package' goal runs all phases up to package, which creates the JAR in the 'target' directory
RUN mvn clean package -DskipTests

# --- STAGE 2: RUNTIME ---
# Use a JRE-only image for the final, lighter runtime
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# 3. Copy the compiled JAR file from the 'builder' stage
# The 'target' directory now exists INSIDE the builder stage
COPY --from=builder /app/target/moneymanger-0.0.1-SNAPSHOT.jar moneymanager-v1.0.jar

# 4. Standard runtime configuration
EXPOSE 9090
ENTRYPOINT ["java", "-jar", "moneymanager-v1.0.jar"]