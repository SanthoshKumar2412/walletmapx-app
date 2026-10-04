# ==============================
# Stage 1: Build
# ==============================
FROM eclipse-temurin:21-jdk AS build

WORKDIR /app

COPY pom.xml .

COPY mvnw .
COPY .mvn .mvn

RUN chmod +x mvnw

RUN ./mvnw dependency:go-offline -B

COPY src src

RUN ./mvnw clean package -DskipTests


# ==============================
# Stage 2: Run
# ==============================
FROM eclipse-temurin:21-jre

WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8082

ENTRYPOINT ["java", "-Duser.timezone=Asia/Kolkata", "-jar", "app.jar"]