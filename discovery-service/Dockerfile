FROM openjdk:8-jdk-alpine
COPY target/discovery-service-0.0.1-SNAPSHOT.jar /app.jar
ENTRYPOINT ["java", "-Xmx100m", "-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]
EXPOSE 9091
