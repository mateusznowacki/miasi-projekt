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
