FROM openjdk:8-jre-alpine

LABEL maintainer="SiropOps <Cyril.Boillat@ville-ge.ch>"

ENV TZ=Europe/Zurich

RUN apk add --update \
    curl \
    && rm -rf /var/cache/apk/*

ADD ./target/app.jar /app/

EXPOSE 3335

HEALTHCHECK --start-period=15s --interval=30s --timeout=3s --retries=3 CMD curl -v --silent http://localhost:3335/actuator/health 2>&1 | grep UP

CMD ["java", "-Xmx128m", "-jar", "/app/app.jar"]