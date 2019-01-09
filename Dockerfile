FROM openjdk:8-jdk-alpine
ADD agile-monkeys-test-1.0.jar agile-monkeys-test.jar
EXPOSE 8080
ENTRYPOINT exec java -jar /agile-monkeys-test.jar