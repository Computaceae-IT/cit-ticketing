FROM openjdk:8-jre-alpine

LABEL maintainer="SiropOps <cyril@botalista.community>"

ENV TZ=Europe/Zurich

RUN apk add --update \
    curl \
    && rm -rf /var/cache/apk/*

ADD ./target/app.jar /app/

EXPOSE 3335

CMD ["java", "-Xms64m", "-Xmx128m", "-jar", "/app/app.jar"]