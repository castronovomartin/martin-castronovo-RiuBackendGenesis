# Stage 1 — Builder
# Compiles the application inside Docker — no local Maven or Java required
FROM eclipse-temurin:21-jdk AS builder

WORKDIR /app

# Copy dependency descriptors first for layer caching
# If pom.xml hasn't changed, Maven dependencies are not re-downloaded
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./

RUN ./mvnw dependency:go-offline -B

# Copy source code and build
COPY src ./src

RUN ./mvnw clean package -DskipTests -B

# Stage 2 — Runtime
# Minimal image containing only the JRE and the compiled JAR
FROM eclipse-temurin:21-jre AS runtime

WORKDIR /app

# Create non-root user for security
RUN groupadd --system appgroup && \
    useradd --system --gid appgroup appuser

# Copy only the compiled JAR from builder stage
COPY --from=builder /app/target/hotel-availability-0.0.1-SNAPSHOT.jar app.jar

# Set non-root user
USER appuser

# Expose application port
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]