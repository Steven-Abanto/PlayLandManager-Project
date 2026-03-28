FROM eclipse-temurin:21-jre

LABEL authors="steve"

WORKDIR /app

COPY target/core-service-*.jar app.jar

EXPOSE 8083

ENTRYPOINT ["java", "-jar", "app.jar"]