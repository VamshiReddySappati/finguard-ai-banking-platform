FROM maven:3.9.11-eclipse-temurin-21 AS build
ARG MODULE
WORKDIR /workspace
COPY pom.xml ./
COPY common-events/pom.xml common-events/pom.xml
COPY common-security/pom.xml common-security/pom.xml
COPY auth-service/pom.xml auth-service/pom.xml
COPY account-service/pom.xml account-service/pom.xml
COPY transaction-service/pom.xml transaction-service/pom.xml
COPY fraud-service/pom.xml fraud-service/pom.xml
COPY audit-service/pom.xml audit-service/pom.xml
COPY investigation-service/pom.xml investigation-service/pom.xml
RUN mvn -B -pl ${MODULE} -am dependency:go-offline
COPY . .
RUN mvn -B -pl ${MODULE} -am clean package -DskipTests

FROM eclipse-temurin:21-jre
ARG MODULE
WORKDIR /app
RUN useradd --system --uid 10001 finguard
COPY --from=build /workspace/${MODULE}/target/app.jar app.jar
USER 10001
EXPOSE 8080
ENTRYPOINT ["java","-XX:MaxRAMPercentage=75.0","-jar","/app/app.jar"]
