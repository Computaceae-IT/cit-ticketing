FROM openjdk:8-jre-alpine

LABEL maintainer="SiropOps <cyril@botalista.community>"

ENV TZ=Europe/Zurich

ADD ./target/app.jar /app/

EXPOSE 3335

CMD ["java", "-Xms64m", "-Xmx128m", "-jar", "/app/app.jar"]