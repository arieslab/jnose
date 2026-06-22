FROM maven:3-eclipse-temurin-21-alpine AS build

WORKDIR /usr/src/app

COPY .mvn/ .mvn/
COPY pom.xml .
COPY src/ src/

RUN mvn clean package -DskipTests -Pstandalone

FROM eclipse-temurin:21-jre-alpine

LABEL maintainer="tassiovirginio@gmail.com"
LABEL version="2.3.0"

WORKDIR /usr/src/app

COPY --from=build /usr/src/app/target/jnose-2.3.0-standalone.jar /usr/src/app/jnose.jar

EXPOSE 8080/tcp

CMD ["java", "-jar", "jnose.jar"]
