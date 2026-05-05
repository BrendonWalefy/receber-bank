FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /workspace
COPY . .
RUN mvn -pl services/payment-service -am package -DskipTests

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /workspace/services/payment-service/target/payment-service-1.0.0.jar app.jar
EXPOSE 8083
ENTRYPOINT ["java", "-jar", "app.jar"]
