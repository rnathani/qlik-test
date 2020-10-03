FROM openjdk:14

ARG JAR_FILE=target/docker-qlik-test.jar

COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
EXPOSE 8080