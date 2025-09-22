FROM openjdk:17-jdk-slim
WORKDIR /app
COPY target/netty_server-0.0.3-SNAPSHOT.jar app.jar
ENV FRUIT_SERVER_HOST=netty-practice-container
CMD ["java", "-jar", "app.jar"]
EXPOSE 8090 9090
