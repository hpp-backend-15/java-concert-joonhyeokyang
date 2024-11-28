FROM bellsoft/liberica-openjdk-alpine:17
# or
# FROM openjdk:8-jdk-alpine
# FROM openjdk:11-jdk-alpine

COPY ./app/build/libs/*.jar app.jar


EXPOSE 8080

ENTRYPOINT ["java","-jar","/app.jar"]