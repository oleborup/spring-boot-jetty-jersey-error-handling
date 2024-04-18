FROM openjdk:17
WORKDIR /app
COPY ./target/ /app
EXPOSE 8080
CMD ["java", "-jar", "spring-boot-jetty-jersey-error-handling-0.1-SNAPSHOT.jar"]
