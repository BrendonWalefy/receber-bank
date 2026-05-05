FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /workspace
COPY . .
RUN mvn -pl services/boleto-service -am package -DskipTests

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /workspace/services/boleto-service/target/boleto-service-1.0.0.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]
