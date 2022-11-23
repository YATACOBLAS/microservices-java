#FROM openjdk:11.0.16
FROM openjdk:11-jdk-alpine as builder
WORKDIR /app/msvc-usuarios
COPY ../pom.xml /app
COPY ./.mvn ./.mvn
COPY ./mvnw .
COPY ./pom.xml .

RUN ./mvnw clean package -Dmaven.test.skip -Dmaven.main.skip -Dspring-boot.repackage.skip && rmdir -r ./target

COPY ./src ./src

RUN ./mvnw clean package -DskipTests


FROM openjdk:11-jdk-alpine

WORKDIR /app

COPY  --from=builder /app/msvc-usuarios/target/msvc-usuarios-0.0.1-SNAPSHOT.jar .

EXPOSE 8001

ENTRYPOINT ["java","-jar","msvc-usuarios-0.0.1-SNAPSHOT.jar"]