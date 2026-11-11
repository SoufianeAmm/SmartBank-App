# Image unique pour tous les modules Spring Boot.
# Construire d'abord les jars :  ./mvnw package -DskipTests
# Puis : docker compose --profile apps up -d --build
FROM eclipse-temurin:17-jre

ARG MODULE
WORKDIR /app
COPY ${MODULE}/target/*.jar app.jar

ENV JAVA_OPTS="-Xms64m -Xmx256m -XX:+UseSerialGC"
ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar /app/app.jar"]
