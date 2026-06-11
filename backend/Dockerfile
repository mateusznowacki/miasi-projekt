FROM eclipse-temurin:21-jdk-alpine as build
WORKDIR /workspace/app

COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
COPY backend backend
COPY shared shared

# Upewniamy się, że skrypt mvnw ma prawa do wykonania i konwertujemy końcówki linii (na wypadek Windowsa)
RUN chmod +x mvnw && sed -i 's/\r$//' mvnw

# Budujemy aplikację z pominięciem testów
RUN ./mvnw install -DskipTests

FROM eclipse-temurin:21-jre-alpine
VOLUME /tmp
COPY --from=build /workspace/app/target/*.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]


eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJmZDI5OTc4OS0wZTFjLTQyZDQtYmYwYy0zMTU1ZDJjNmNkODYiLCJyb2xlIjoiUEFUSUVOVCIsImlhdCI6MTc4MTIwNTU5OCwiZXhwIjoxNzgxMjkxOTk4fQ.q8HPumtgeI5c6gUJieNzaJjX543HzrGuswjs-d2HK_s