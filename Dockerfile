FROM eclipse-temurin:21-jre

WORKDIR /app

# build the jar first with ./gradlew bootJar
COPY build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
