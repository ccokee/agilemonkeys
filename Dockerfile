FROM openjdk:8-jdk-alpine
EXPOSE 8080
RUN mkdir -p /opt/app
WORKDIR /opt/app
COPY ./build/libs/agile-monkeys-test-1.0.jar ./
ENTRYPOINT exec java -jar agile-monkeys-test-1.0.jar
