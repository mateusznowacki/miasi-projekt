# Build stage
FROM maven:3-eclipse-temurin-25 AS build
WORKDIR /app

# Skopiowanie plików (uruchamiając budowanie z poziomu folderu backend)
COPY pom.xml .
COPY src ./src

# Zbudowanie aplikacji
RUN mvn clean package -DskipTests

# Run stage
FROM eclipse-temurin:25-jre-jammy
WORKDIR /app

# Skopiowanie zbudowanego pliku JAR
COPY --from=build /app/target/*.jar app.jar

# Wystawienie portu
EXPOSE 8080

# Uruchomienie aplikacji
ENTRYPOINT ["java", "-jar", "app.jar"]
