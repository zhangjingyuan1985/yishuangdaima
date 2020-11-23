FROM openjdk:8-jdk-alpine
MAINTAINER litterqa <hubo@sutpc.com>
RUN echo "Asia/Shanghai" > /etc/timezone
ADD target/sutpc-data-bus-gps-pta-file-1.0-SNAPSHOT.jar /app/app.jar
CMD ["java", "-jar", "/app/app.jar", "--user.timezone=GMT+08"]
EXPOSE 8799