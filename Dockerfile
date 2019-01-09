FROM gradle:4.8-jdk8-alpine as builder
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle build -x test

FROM openjdk:8-jdk-alpine
EXPOSE 8080
COPY --from=builder /home/gradle/src/build/libs/agile-monkeys-test-1.0.jar /app/
WORKDIR /app
ENTRYPOINT exec java -jar agile-monkeys-test-1.0.jar
