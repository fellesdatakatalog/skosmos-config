FROM maven:3.9-eclipse-temurin-22-alpine AS builder

WORKDIR /app

COPY ./apps/skosmos-store/pom.xml ./pom.xml
COPY ./apps/skosmos-store/src ./src

RUN mvn clean install

FROM eclipse-temurin:22-jre-alpine

ARG USER=default
ENV HOME=/home/$USER
WORKDIR $HOME

ENV TZ=Europe/Oslo
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

# install sudo as root
RUN apk add --update sudo

RUN adduser -D $USER \
        && echo "$USER ALL=(ALL) NOPASSWD: ALL" > /etc/sudoers.d/$USER \
        && chmod 0440 /etc/sudoers.d/$USER

USER $USER

COPY --from=builder --chown=$USER app/target/app.jar app.jar
CMD ["java", "-XX:+UseZGC", "-Xmx2g", "-jar", "app.jar"]
