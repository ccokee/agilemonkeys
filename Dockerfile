FROM gradle:4.8-jdk8-alpine as builder
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle build -x test

FROM openjdk:8-jdk-alpine

ARG USER=gradle
ENV HOME /home/$USER

# install sudo as root
RUN apk add --update sudo

# add new user
RUN adduser -D $USER \
        && echo "$USER ALL=(ALL) NOPASSWD: ALL" > /etc/sudoers.d/$USER \
        && chmod 0440 /etc/sudoers.d/$USER

USER $USER
WORKDIR $HOME

EXPOSE 8080
COPY --from=builder /home/gradle/src/build/libs/agile-monkeys-test-1.0.jar $HOME/app/
WORKDIR $HOME/app
ENTRYPOINT exec java -jar agile-monkeys-test-1.0.jar
