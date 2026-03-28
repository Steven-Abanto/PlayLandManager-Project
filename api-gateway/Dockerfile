FROM eclipse-temurin:21-jre

LABEL authors="steve"

WORKDIR /app

COPY target/api-gateway-*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]