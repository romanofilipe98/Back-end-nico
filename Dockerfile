# Fase de compilação (Build)
FROM maven:3.8.5-openjdk-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Fase de execução (Run)
FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

# Expõe a porta que o Render vai usar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]