FROM eclipse-temurin:21-jre

LABEL authors="steve"

WORKDIR /app

COPY target/catalogo-service-*.jar app.jar

EXPOSE 8082

ENTRYPOINT ["java", "-jar", "app.jar"]