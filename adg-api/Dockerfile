FROM maven:3.8.5-jdk-11-slim AS build
COPY src /adg-api/src
COPY pom.xml /adg-api
RUN mvn -f /adg-api/pom.xml clean install

FROM openjdk:11-jre-slim
COPY --from=build /adg-api/target/adg-api-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8082
ENTRYPOINT ["java", "-Xms256M", "-Xmx512M", "-Dspring.profiles.active=api-dev", "-jar" , "app.jar", ]